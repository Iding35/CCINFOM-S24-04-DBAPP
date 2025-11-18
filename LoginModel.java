import java.sql.*;

import javax.swing.JOptionPane;

public class LoginModel {
	
	private DBConnect db = new DBConnect();
	
	public boolean checkCustomer(String email, String password) {
    	String sql = "SELECT * FROM Customers WHERE email = ? AND password = ?";
    	try {
    		Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql);
    		
    		stmt.setString(1, email);
    		stmt.setString(2, password);
    		
    		try(ResultSet rs = stmt.executeQuery()){
    			return rs.next();
    		}
    	}
    	catch (SQLException e) {
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    	}
    	return false;
    }
	
	/**
     * Gets the customer_id for a given email.
     * @param email The customer's email.
     * @return The customer_id, or -1 if not found.
     */
    public int getCustomerId(String email) {
        String sql = "SELECT customer_id FROM Customers WHERE email = ?";
        try {
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1; // Not found
    }
}
