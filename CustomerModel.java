import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class CustomerModel {

    private Connection connection;

    public CustomerModel() {

        DBConnect db = new DBConnect();
        try {
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


    public DefaultTableModel getCartItems(int customerId) throws SQLException {
        String sql = "SELECT p.name, p.price, c.quantity, (p.price * c.quantity) AS total_item_price " +
                     "FROM Cart c " +
                     "JOIN Products p ON c.product_id = p.product_id " +
                     "WHERE c.customer_id = " + customerId; // Simple for now
        return getTableData(sql);
    }
    
   
    public boolean addToCart(int customerId, int productId, int quantity) {
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
    public boolean placeOrder(int customerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // 1. Start Transaction
            connection.setAutoCommit(false); 

            // --- A. Get Customer's Address & Calculate Total ---
            // We assume the shipping address is the customer's main address for now
            int addressId = 0;
            String addressSql = "SELECT address_id FROM Customers WHERE customer_id = ?";
            ps = connection.prepareStatement(addressSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                addressId = rs.getInt("address_id");
            } else {
                connection.rollback();
                return false; // Customer not found
            }
            rs.close();
            ps.close();

            // Calculate Total Price from Cart
            double totalPrice = 0;
            String totalSql = "SELECT SUM(c.quantity * p.price) FROM Cart c JOIN Products p ON c.product_id = p.product_id WHERE c.customer_id = ?";
            ps = connection.prepareStatement(totalSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                totalPrice = rs.getDouble(1);
            }
            rs.close();
            ps.close();

            if (totalPrice == 0) {
                connection.rollback();
                return false; // Cart is empty
            }

            // --- B. Create the Order Record ---
            String insertOrderSql = "INSERT INTO Orders (total_price, status, order_datetime, customer_id, shipping_address_id) VALUES (?, 'Pending', NOW(), ?, ?)";
            ps = connection.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
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

            // --- C. Move Items from Cart to Order_Details ---
            // We need to fetch cart items first to get prices and product IDs
            String cartSql = "SELECT c.product_id, c.quantity, p.price FROM Cart c JOIN Products p ON c.product_id = p.product_id WHERE c.customer_id = ?";
            ps = connection.prepareStatement(cartSql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            String insertDetailSql = "INSERT INTO Order_Details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetail = connection.prepareStatement(insertDetailSql);
            
            String updateStockSql = "UPDATE Products SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement psStock = connection.prepareStatement(updateStockSql);

            while (rs.next()) {
                int prodId = rs.getInt("product_id");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");

                // Add to Order_Details
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, prodId);
                psDetail.setInt(3, qty);
                psDetail.setDouble(4, price);
                psDetail.addBatch();

                // Subtract Stock
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
            ps = connection.prepareStatement(deleteCartSql);
            ps.setInt(1, customerId);
            ps.executeUpdate();
            ps.close();

            // 2. Commit Transaction
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) connection.rollback(); // Undo everything if error
            } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true); // Reset default
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
    /**
     * Fetches the customer's profile and address as a formatted String.
     * @param customerId The ID of the logged-in customer.
     * @return A String containing the formatted profile info.
     */
    public String getCustomerProfile(int customerId) {
        StringBuilder sb = new StringBuilder();
        // Join Customers and Addresses tables to get full info
        String sql = "SELECT c.first_name, c.last_name, c.email, c.phone_number, " +
                     "a.street, a.city, a.zip_code " +
                     "FROM Customers c " +
                     "JOIN Addresses a ON c.address_id = a.address_id " +
                     "WHERE c.customer_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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