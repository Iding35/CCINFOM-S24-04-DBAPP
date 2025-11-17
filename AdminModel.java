import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
    
    public DefaultTableModel getSupplierListing() throws SQLException {
        String sql = "SELECT * FROM suppliers ORDER BY supplier_id";
        return getTableData(sql);
    }
    
    public DefaultTableModel getVehicleListing() throws SQLException {
        String sql = "SELECT * FROM vehicles ORDER BY vehicle_id";
        return getTableData(sql);
    }
    
    public DefaultTableModel getOrderListing() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY order_datetime DESC";
        return getTableData(sql);
    }
    
    public DefaultTableModel getSupplierShipmentListing() throws SQLException {
        String sql = "SELECT ss.supplier_shipment_id, ss.product_id, p.name AS product_name, ss.supplier_id, s.company_name, FORMAT(ss.product_cost, 2) AS product_cost, ss.quantity, d.shipping_datetime, d.arrival_datetime\r\n"
        		+ "FROM supplier_shipment ss\r\n"
        		+ "JOIN delivery_info d\r\n"
        		+ "ON d.delivery_id = ss.delivery_id\r\n"
        		+ "JOIN suppliers s\r\n"
        		+ "ON s.supplier_id = ss.supplier_id\r\n"
        		+ "JOIN products p\r\n"
        		+ "ON p.product_id = ss.product_id\r\n"
        		+ "ORDER BY arrival_datetime DESC;";
        return getTableData(sql);
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
    
    public boolean createNewProduct(String name, String description, String brand, float price, String category) {
    	
    	String sql = "INSERT INTO Products (name, description, brand, price, quantity, category)\r\n"
    			+ "VALUES"
    			+ "(?, ?, ?, ?, 0, ?)";
    	try(Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql)){
    		stmt.setString(1, name);
    		stmt.setString(2, description);
    		stmt.setString(3, brand);
    		stmt.setFloat(4, price);
    		stmt.setString(5, category);
    		
    		return stmt.executeUpdate() > 0;
    	}
    	catch(SQLException e) {
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
    	}
    }
    
    //FOR SUPPLIER ADD VALIDATION FOR PHONE AND EMAIL
    //public boolean isValidPhone(String phone)
    //public boolean isValidEmail(String email)
    //public boolean createNewSupplier(String name, String phone, String email, String street, String city, int zipCode)
    /*to create another suppler, you must insert the address of the supplier sa address table
    	tapos retrieve the address_id then insert the rest of the attributes for the supplier
    */
    
    
    //FOR VEHICLE ADD VALIDATION FOR PLATE NUMBER
    //public boolean isValidPlate(String plate)
    //public boolean createNewVehicle(String plate_num, String type, String email, String status)
   
    public boolean checkValidVehicle(int vehicleID) {
    	String sql = "SELECT * FROM Vehicles WHERE vehicle_id = ? AND status = 'Available'";
    	try(Connection conn = db.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(sql)){
    		
        	stmt.setInt(1, vehicleID);
    		try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
    	}catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
    
    public boolean restockShippment(int productID, int supplierID, int vehicleID, int quantity, int cost, String shippingDateTime) {

    	String sqlInsertDeliveryInfo = "INSERT INTO Delivery_Info (vehicle_id, shipping_datetime, arrival_datetime, status)\r\n"
    			+ "VALUES"
    			+ "(?, ?, NULL, 'Shipping')";
   
    	String sqlInsertSupplierShipment = "INSERT INTO Supplier_Shipment (product_id, supplier_id, product_cost, quantity, delivery_id)\r\n"
    			+ "VALUES"
    			+ "(?, ?, ?, ?, ?)";
    	
    	
    	int deliveryInfoID = -1;
    	
    	try(Connection conn = db.getConnection()){
    		conn.setAutoCommit(false);
    		
    		//insert address of supplier
    		try(PreparedStatement stmtDelivery = conn.prepareStatement(sqlInsertDeliveryInfo, PreparedStatement.RETURN_GENERATED_KEYS)){
    			stmtDelivery.setInt(1, vehicleID);
    			//convert the string into timestamp
    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime ldt = LocalDateTime.parse(shippingDateTime, formatter);
                Timestamp timestamp = Timestamp.valueOf(ldt);
    			stmtDelivery.setTimestamp(2, timestamp);
    			
    			int affectedRows = stmtDelivery.executeUpdate();
    			
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Delivery_Info insertion failed (0 rows affected).");
    				return false;
    			}
    			
    			//retrieve address_id
        		try(ResultSet generatedAddressID = stmtDelivery.getGeneratedKeys()){
        			if(generatedAddressID.next()) {
        				deliveryInfoID = generatedAddressID.getInt(1);
        			}
        			else {
        				conn.rollback();
        				throw new SQLException("Failed to retrieve auto-generated delivery_id.");
        			}
        		}
    		}
    		
    		//Insert supplier_shipment
    		try(PreparedStatement stmtSupplier = conn.prepareStatement(sqlInsertSupplierShipment)){
    			stmtSupplier.setInt(1, productID);
    			stmtSupplier.setInt(2, supplierID);
    			stmtSupplier.setInt(3, cost);
    			stmtSupplier.setInt(4, quantity);
    			stmtSupplier.setInt(5, deliveryInfoID);
    			
    			int affectedRows = stmtSupplier.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Delivery_Info insertion failed (0 rows affected).");
    				return false;
    			}
    			
    		}
    		
    		conn.commit();
    		return true;

    		
    	}catch(SQLException e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    	}
    	
    	return false;
    }
    
    public boolean isDeliveryComplete(int supplierShipmentID) {
        String sql = "SELECT di.status " 
                + "FROM delivery_info di "
                + "JOIN supplier_shipment ss "
                + "ON di.delivery_id = ss.delivery_id "
                + "WHERE ss.supplier_shipment_id = ?;";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierShipmentID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status"); 
                    
                    return "Complete".equalsIgnoreCase(status); 
                } else {
                    // No shipment found with that ID
                    return false; 
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean restockArrivalUpdate(int supplierShipmentID, String arrivalDateTime) {
    	
    	String sqlSupplierShipment = "SELECT delivery_id, product_id, quantity \r\n"
    			+ "FROM Supplier_Shipment \r\n"
    			+ "WHERE supplier_shipment_id = ?";
    	
    	String sqlDelivery = "UPDATE Delivery_Info \r\n"
    			+ "SET arrival_datetime = ?, status = 'Complete'\r\n"
    			+ "WHERE delivery_id = ?";
    	
    	String sqlUpdateProductStocks = "UPDATE Products \r\n"
    			+ "SET quantity = quantity + ? \r\n"
    			+ "WHERE product_id = ?";
    	
    	int deliveryInfoID = -1;
    	int productID = -1;
    	int quantity = 0;
    	
    	try(Connection conn = db.getConnection()){
    		conn.setAutoCommit(false);
    		
    		//insert address of supplier
    		try(PreparedStatement stmtDelivery = conn.prepareStatement(sqlSupplierShipment)){
    			stmtDelivery.setInt(1, supplierShipmentID);

    			//retrieve delivery_id
        		try(ResultSet rs = stmtDelivery.executeQuery()){
        			if(rs.next()) {
        				deliveryInfoID = rs.getInt("delivery_id");
        				productID = rs.getInt("product_id");
        				quantity = rs.getInt("quantity");
        				
        			}
        			else {
        				conn.rollback();
                        System.err.println("No shipment details found for supplier_shipment_id: " + supplierShipmentID);
                        return false;
        			}
        		}
    		}
    		
    		//Update supplier_shipment arrive_dateteime
    		try(PreparedStatement stmtSupplier = conn.prepareStatement(sqlDelivery)){
    			
    			//convert the string into timestamp
    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime ldt = LocalDateTime.parse(arrivalDateTime, formatter);
                Timestamp timestamp = Timestamp.valueOf(ldt);
                stmtSupplier.setTimestamp(1, timestamp);
                stmtSupplier.setInt(2, deliveryInfoID);
                
    			int affectedRows = stmtSupplier.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Arrival Date Time update failed (0 rows affected).");
    				return false;
    			}
    			
    		}
    		
    		//Update product quantity 
    		try(PreparedStatement stmtProduct = conn.prepareStatement(sqlUpdateProductStocks)){
    			
    			stmtProduct.setInt(1, quantity);
    			stmtProduct.setInt(2, productID);
                
    			int affectedRows = stmtProduct.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Product quantity update failed (0 rows affected).");
    				return false;
    			}
    			
    		}
    		
    		conn.commit();
    		return true;

    		
    	}catch(SQLException e) {
    		e.printStackTrace();
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
    
    public double getTotalRevenue(String month, String year) throws SQLException{
    	String sql = "SELECT SUM(total_price) \r\n"
    			+ "FROM Orders\r\n"
    			+ "WHERE MONTH(order_datetime) = ? AND YEAR(order_datetime) = ? AND status = 'Completed';";
    	
    	try(Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql)){
    		
    		stmt.setString(1, month);
    		stmt.setString(2, year);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1); // Return 0.0 if SUM returns NULL
                }
            }
    	}
    	
    	return 0.00; // Return 0.0 if SUM returns NULL
    }
    
	public int getTotalOrder(String month, String year) throws SQLException{
		String sql = "SELECT COUNT(order_id)\r\n"
				+ "FROM Orders\r\n"
				+ "WHERE MONTH(order_datetime) = ? AND YEAR(order_datetime) = ? AND status = 'Completed';\r\n";
		
		try(Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql)){
    		
    		stmt.setString(1, month);
    		stmt.setString(2, year);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return 0.0 if SUM returns NULL
                }
            }
    	}
		
	    return 0;
	}

}
