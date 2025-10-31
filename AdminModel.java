import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminModel {
    private DBConnect db = new DBConnect();
    
    public DefaultTableModel getTableData(String query) throws SQLException {
        
        DefaultTableModel model = new DefaultTableModel();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
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
        return model;
    }
    
    public DefaultTableModel getProductListing() throws SQLException {
        String sql = "SELECT * FROM products ORDER BY product_id";
        return getTableData(sql);
    }
    
    //DI PA NAINSERT SA DATABASE UNG ORDER
    public DefaultTableModel getOrderListing() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY order_datetime DESC";
        return getTableData(sql);
    }
    
    public DefaultTableModel getProductSupplierListing() throws SQLException {
        String sql = "SELECT * FROM product_supplier ORDER BY order_datetime DESC";
        return getTableData(sql);
    }
    
    public boolean checkProductId(int id) {
    	String sql = "SELECT * FROM Products WHERE product_id = ?";
    	try {
    		Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql);
    		
    		stmt.setInt(1, id);
    		
    		try(ResultSet rs = stmt.executeQuery()){
    			return rs.next();
    		}
    	}
    	catch (SQLException e) {
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    	}
    	return false;
    }
    
    private boolean isValidFieldName(String field) {
        return field.matches("name|description|brand|price|quantity|category");
    }
    
    public boolean updateProductDetail(int productId, String fieldName, String newValue) {
    	if(!isValidFieldName(fieldName)) {
    		System.err.println("Invalid field name provided: " + fieldName);
            return false;
    	}
    	
    	String sql = "UPDATE Products SET " + fieldName + " = ? WHERE product_id = ?";
    	try(Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql)){
    		stmt.setString(1, newValue);
    		stmt.setInt(2, productId);
    		return stmt.executeUpdate() > 0;
    	}
    	catch(SQLException e) {
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
    	}
    }
    
    public boolean checkOrderId(int id) {
    	String sql = "SELECT * FROM Orders WHERE order_id = ?";
    	try {
    		Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql);
    		
    		stmt.setInt(1, id);
    		
    		try(ResultSet rs = stmt.executeQuery()){
    			return rs.next();
    		}
    	}
    	catch (SQLException e) {
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    	}
    	return false;
    }


    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {        // Since 'status' is an ENUM, we pass the new status string directly
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    

}