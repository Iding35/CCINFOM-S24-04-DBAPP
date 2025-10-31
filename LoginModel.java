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
}
