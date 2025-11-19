import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class CustomerModel {

    // Using a field connection for simplicity, though pooling/passing connections is robust
    private Connection connection; 
    
    // Using a fresh DBConnect instance inside methods is often more robust for transaction management
    private DBConnect db = new DBConnect(); 

    public CustomerModel() {
        try {
            // Establish the initial connection (following existing pattern)
            this.connection = db.getConnection(); 
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database in CustomerModel.");
            e.printStackTrace();
        }
        
        if (connection == null) {
            System.err.println("Database connection is null in CustomerModel.");
        }
    }


    private DefaultTableModel getTableData(String query) throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        // Since connection is a field, we manage the Prepared/ResultSet resources
        try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnLabel(i));
            }

            // Add rows
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
        }
        return model;
    }


    public DefaultTableModel getAllProducts() throws SQLException {
        String sql = "SELECT product_id, name, description, brand, price, quantity, category FROM Products WHERE quantity > 0";
        return getTableData(sql);
    }

    /**
     * Retrieves cart items. Includes p.product_id as the first column for removal logic.
     */
    public DefaultTableModel getCartItems(int customerId) throws SQLException {
        // CRITICAL FIX: Added p.product_id for removal logic
        String sql = "SELECT p.product_id, p.name, p.price, c.quantity, (p.price * c.quantity) AS total_item_price " +
                     "FROM Cart c " +
                     "JOIN Products p ON c.product_id = p.product_id " +
                     "WHERE c.customer_id = " + customerId; 
        return getTableData(sql);
    }
    
   
    public boolean addToCart(int customerId, int productId, int quantity) {
        // This simple INSERT assumes (customer_id, product_id) is a primary/unique key 
        // to prevent duplicate rows for the same product, relying on MySQL's error handling.
        String sql = "INSERT INTO Cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * NEW: Decrements the quantity of a product in the cart by 1. 
     * If the quantity hits 1, it performs a DELETE.
     */
    public boolean decrementCartQuantity(int customerId, int productId) {
        String checkSql = "SELECT quantity FROM Cart WHERE customer_id = ? AND product_id = ?";
        String updateSql = "UPDATE Cart SET quantity = quantity - 1 WHERE customer_id = ? AND product_id = ?";
        String deleteSql = "DELETE FROM Cart WHERE customer_id = ? AND product_id = ?";
        
        // Using a try-with-resources block for connection management within the method is safer
        try (Connection conn = db.getConnection()) { 
            conn.setAutoCommit(false);
            
            // --- Step 1: Check Current Quantity ---
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, customerId);
                psCheck.setInt(2, productId);
                ResultSet rs = psCheck.executeQuery();
                
                if (rs.next()) {
                    int currentQuantity = rs.getInt("quantity");
                    
                    if (currentQuantity > 1) {
                        // --- Step 2: Reduce Quantity ---
                        try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                            psUpdate.setInt(1, customerId);
                            psUpdate.setInt(2, productId);
                            psUpdate.executeUpdate();
                        }
                    } else if (currentQuantity == 1) {
                        // --- Step 3: Delete Item (if quantity hits 1) ---
                        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                            psDelete.setInt(1, customerId);
                            psDelete.setInt(2, productId);
                            psDelete.executeUpdate();
                        }
                    } else {
                        conn.rollback();
                        return false; 
                    }
                } else {
                    conn.rollback();
                    return false; // Item not found in cart
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Since we established a new connection here, it handles rollback on its own
            return false;
        } 
    }

    public boolean placeOrder(int customerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try (Connection conn = db.getConnection()){
            // 1. Start Transaction
            conn.setAutoCommit(false); 

            // --- A. Get Customer's Address & Calculate Total ---
            int addressId = 0;
            String addressSql = "SELECT address_id FROM Customers WHERE customer_id = ?";
            ps = conn.prepareStatement(addressSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                addressId = rs.getInt("address_id");
            } else {
                conn.rollback();
                return false; 
            }
            rs.close();
            ps.close();

            // Calculate Total Price from Cart
            double totalPrice = 0;
            String totalSql = "SELECT SUM(c.quantity * p.price) FROM Cart c JOIN Products p ON c.product_id = p.product_id WHERE c.customer_id = ?";
            ps = conn.prepareStatement(totalSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                totalPrice = rs.getDouble(1);
            }
            rs.close();
            ps.close();

            if (totalPrice == 0) {
                conn.rollback();
                return false; // Cart is empty
            }

            // --- B. Create the Order Record ---
            // BUSINESS RULE: Order transactions can not be removed once inserted.
            String insertOrderSql = "INSERT INTO Orders (total_price, status, order_datetime, customer_id, shipping_address_id) VALUES (?, 'Pending', NOW(), ?, ?)";
            ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, totalPrice);
            ps.setInt(2, customerId);
            ps.setInt(3, addressId);
            ps.executeUpdate();

            // Get the generated Order ID
            int orderId = 0;
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            rs.close();
            ps.close();

            // --- C. Move Items from Cart to Order_Details & Update Stock ---
            String cartSql = "SELECT c.product_id, c.quantity, p.price FROM Cart c JOIN Products p ON c.product_id = p.product_id WHERE c.customer_id = ?";
            ps = conn.prepareStatement(cartSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            String insertDetailSql = "INSERT INTO Order_Details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(insertDetailSql);
            
            String updateStockSql = "UPDATE Products SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement psStock = conn.prepareStatement(updateStockSql);

            while (rs.next()) {
                int prodId = rs.getInt("product_id");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");

                psDetail.setInt(1, orderId);
                psDetail.setInt(2, prodId);
                psDetail.setInt(3, qty);
                psDetail.setDouble(4, price);
                psDetail.addBatch();

                psStock.setInt(1, qty);
                psStock.setInt(2, prodId);
                psStock.addBatch();
            }
            psDetail.executeBatch();
            psStock.executeBatch();
            
            psDetail.close();
            psStock.close();
            rs.close();
            ps.close();

            // --- D. Clear the Cart ---
            String deleteCartSql = "DELETE FROM Cart WHERE customer_id = ?";
            ps = conn.prepareStatement(deleteCartSql);
            ps.setInt(1, customerId);
            ps.executeUpdate();
            ps.close();

            // 2. Commit Transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * NEW: Fetches the customer's order history as a table model.
     */
    public DefaultTableModel getCustomerOrderHistory(int customerId) throws SQLException {
        // Fetches essential order data for the "My Orders" view.
        String sql = "SELECT order_id, total_price, status, order_datetime " +
                     "FROM Orders " +
                     "WHERE customer_id = ? " +
                     "ORDER BY order_datetime DESC";
        
        DefaultTableModel model = new DefaultTableModel();
        try (Connection conn = db.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Add column names
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnLabel(i));
                }

                // Add rows
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }
            }
        }
        return model;
    }

    /**
     * Fetches the customer's profile and address as a formatted String.
     */
    public String getCustomerProfile(int customerId) {
        StringBuilder sb = new StringBuilder();
        // Join Customers and Addresses tables to get full info
        String sql = "SELECT c.first_name, c.last_name, c.email, c.phone_number, " +
                     "a.street, a.city, a.zip_code " +
                     "FROM Customers c " +
                     "JOIN Addresses a ON c.address_id = a.address_id " +
                     "WHERE c.customer_id = ?";
        
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sb.append("--- Customer Profile ---\n");
                    sb.append("Name: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                    sb.append("Email: ").append(rs.getString("email")).append("\n");
                    sb.append("Phone: ").append(rs.getString("phone_number")).append("\n\n");
                    
                    sb.append("--- Shipping Address ---\n");
                    sb.append("Street: ").append(rs.getString("street")).append("\n");
                    sb.append("City:   ").append(rs.getString("city")).append("\n");
                    sb.append("Zip:    ").append(rs.getString("zip_code"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error loading profile.";
        }
        return sb.toString();
    }
    
}