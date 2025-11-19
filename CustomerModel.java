import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.table.DefaultTableModel;

public class CustomerModel {

    private DBConnect db = new DBConnect(); 
    private Connection connection; 

    public CustomerModel() {
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
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
            return model;
        }
    }


    public DefaultTableModel getAllProducts() throws SQLException {
        String sql = "SELECT product_id, name, description, brand, price, quantity, category FROM Products WHERE quantity > 0";
        return getTableData(sql);
    }

    public DefaultTableModel getCartItems(int customerId) throws SQLException {
        String sql = "SELECT p.product_id, p.name, p.price, c.quantity, (p.price * c.quantity) AS total_item_price " +
                     "FROM Cart c " +
                     "JOIN Products p ON c.product_id = p.product_id " +
                     "WHERE c.customer_id = " + customerId; 
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
    

    public boolean removeFullItem(int customerId, int productId) {
        String sql = "DELETE FROM Cart WHERE customer_id = ? AND product_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            
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
        
        try (Connection conn = db.getConnection()){
            conn.setAutoCommit(false); 

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

            String insertOrderSql = "INSERT INTO Orders (total_price, status, order_datetime, customer_id, shipping_address_id, payment_status) VALUES (?, 'Pending', NOW(), ?, ?, 'Not Paid')";
            ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, totalPrice);
            ps.setInt(2, customerId);
            ps.setInt(3, addressId);
            ps.executeUpdate();

            int orderId = 0;
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            rs.close();
            ps.close();

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

            String deleteCartSql = "DELETE FROM Cart WHERE customer_id = ?";
            ps = conn.prepareStatement(deleteCartSql);
            ps.setInt(1, customerId);
            ps.executeUpdate();
            ps.close();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public DefaultTableModel getCustomerOrderHistory(int customerId) throws SQLException {
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

                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnLabel(i));
                }

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


    public String getCustomerDeliveryDate(int orderId) throws SQLException {
        String sql = "SELECT di.arrival_datetime " +
                     "FROM Orders o " +
                     "JOIN Shipping s ON o.order_id = s.order_id " +
                     "JOIN Delivery_Info di ON s.delivery_id = di.delivery_id " +
                     "WHERE o.order_id = ? AND di.status = 'Complete'";
        
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("arrival_datetime");
                    return ts != null ? ts.toString().split("\\.")[0] : null; 
                }
            }
        }
        return null; 
    }


    public boolean isPastReturnDeadline(int orderId) throws SQLException {
        String deliveryDateStr = getCustomerDeliveryDate(orderId);
        
        if (deliveryDateStr == null) {
            return true; 
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime deliveryDateTime = LocalDateTime.parse(deliveryDateStr, formatter);
                                            
            LocalDateTime returnDeadline = deliveryDateTime.plusDays(7);
            
            return LocalDateTime.now().isAfter(returnDeadline);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + deliveryDateStr);
            e.printStackTrace();
            return true; 
        }
    }

    public String getCustomerProfile(int customerId) {
        StringBuilder sb = new StringBuilder();
        
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