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
        String sql = "SELECT p.name, p.price, c.quantity, (p.price * c.query) AS total_item_price " +
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
    
}