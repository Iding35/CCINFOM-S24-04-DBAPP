import java.sql.*;

public class DBConnect {
	private static final String URL = "jdbc:mysql://localhost:3306/dbhardware";
    private static final String USER = "root";
    private static final String PASSWORD = "koro sensei";
    
    public Connection getConnection() throws SQLException {
    	Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }

    	return conn;
    	
    }
}
