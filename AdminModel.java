
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
        String sql = "SELECT * FROM admin_product";
        return getTableData(sql);
    }
    
    public DefaultTableModel getSupplierListing() throws SQLException {
        // Joins address so the table looks nicer
        String sql = "SELECT s.supplier_id, s.company_name, s.phone_number, s.email, " +
                     "CONCAT(a.street, ', ', a.city, ' ', a.zip_code) AS address " +
                     "FROM Suppliers s " +
                     "JOIN Addresses a ON s.address_id = a.address_id " +
                     "ORDER BY s.supplier_id";
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
        String sql = "SELECT * FROM admin_supplier_shipment";
        return getTableData(sql);
    }
 
    private boolean isValidFieldName(String field) {
        return field.matches("name|description|brand|price|quantity|category|status");
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
    
    public boolean createNewProduct(String name, String description, String brand, float price, String category, String status) {
    	
    	String sql = "INSERT INTO Products (name, description, brand, price, quantity, category, status)\r\n"
    			+ "VALUES"
    			+ "(?, ?, ?, ?, 0, ?, ?)";
    	try(Connection conn = db.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(sql)){
    		stmt.setString(1, name);
    		stmt.setString(2, description);
    		stmt.setString(3, brand);
    		stmt.setFloat(4, price);
    		stmt.setString(5, category);
    		stmt.setString(6, status);
    		
    		return stmt.executeUpdate() > 0;
    	}
    	catch(SQLException e) {
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
    	}
    }
    
    public boolean isSupplierEmailUnique(String email) {
        String sql = "SELECT 1 FROM Suppliers WHERE email = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // Returns true if NO record found (Unique)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
    
    public boolean isSupplierPhoneUnique(String phone) {
        String sql = "SELECT 1 FROM Suppliers WHERE phone_number = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // Returns true if NO record found (Unique)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createNewSupplier(String name, String phone, String email, String street, String city, String zipCode) {
        String sqlAddress = "INSERT INTO Addresses (street, city, zip_code) VALUES (?, ?, ?)";
        String sqlSupplier = "INSERT INTO Suppliers (company_name, phone_number, email, address_id) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmtAddr = null;
        PreparedStatement stmtSup = null;
        ResultSet generatedKeys = null;

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Address
            stmtAddr = conn.prepareStatement(sqlAddress, Statement.RETURN_GENERATED_KEYS);
            stmtAddr.setString(1, street);
            stmtAddr.setString(2, city);
            stmtAddr.setString(3, zipCode);
            
            int affectedRows = stmtAddr.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            // 2. Retrieve Address ID
            generatedKeys = stmtAddr.getGeneratedKeys();
            int addressId;
            if (generatedKeys.next()) {
                addressId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating address failed, no ID obtained.");
            }

            // 3. Insert Supplier
            stmtSup = conn.prepareStatement(sqlSupplier);
            stmtSup.setString(1, name);
            stmtSup.setString(2, phone);
            stmtSup.setString(3, email);
            stmtSup.setInt(4, addressId);
            
            stmtSup.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmtAddr != null) stmtAddr.close();
                if (stmtSup != null) stmtSup.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    
    public boolean isValidPlate(String plate) {
        String sql = "SELECT 1 FROM Vehicles WHERE plate_number = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // Returns true if NO record found (Unique)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createNewVehicle(String plate, String type, String status) {
        String sql = "INSERT INTO Vehicles (plate_number, type, status) VALUES (?, ?, ?)";
        try(Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, plate);
            stmt.setString(2, type);
            stmt.setString(3, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public String[] getAvailableVehicleList() {
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT vehicle_id, plate_number FROM Vehicles WHERE status = 'Available'";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("vehicle_id") + " - " + rs.getString("plate_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.toArray(new String[0]);
    }

    public boolean shipOrder(int orderId, int vehicleId) {
        // 1. Create Delivery_Info record (Status: Shipping)
        String sqlDelivery = "INSERT INTO Delivery_Info (vehicle_id, shipping_datetime, status) VALUES (?, NOW(), 'Shipping')";
        // 2. Create Shipping record linking order and delivery
        String sqlShipping = "INSERT INTO Shipping (order_id, delivery_id) VALUES (?, ?)";
        // 3. Update Order status
        String sqlUpdateOrder = "UPDATE Orders SET status = 'Shipping' WHERE order_id = ?";
        // 4. Update Vehicle status
        String sqlUpdateVehicle = "UPDATE Vehicles SET status = 'Occupied' WHERE vehicle_id = ?";

        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            int deliveryId = -1;
            // Insert Delivery_Info
            try (PreparedStatement stmt = conn.prepareStatement(sqlDelivery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, vehicleId);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) deliveryId = rs.getInt(1);
                    else throw new SQLException("Failed to create delivery record");
                }
            }

            // Insert Shipping
            try (PreparedStatement stmt = conn.prepareStatement(sqlShipping)) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, deliveryId);
                stmt.executeUpdate();
            }

            // Update Order
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOrder)) {
                stmt.setInt(1, orderId);
                stmt.executeUpdate();
            }

            // Update Vehicle
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateVehicle)) {
                stmt.setInt(1, vehicleId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean completeOrder(int orderId) {
        String sqlGetIds = "SELECT d.delivery_id, d.vehicle_id FROM Shipping s " +
                           "JOIN Delivery_Info d ON s.delivery_id = d.delivery_id " +
                           "WHERE s.order_id = ?";
        String sqlUpdateOrder = "UPDATE Orders SET status = 'Completed' WHERE order_id = ?";
        String sqlUpdateDelivery = "UPDATE Delivery_Info SET arrival_datetime = NOW(), status = 'Complete' WHERE delivery_id = ?";
        String sqlUpdateVehicle = "UPDATE Vehicles SET status = 'Available' WHERE vehicle_id = ?";

        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            int deliveryId = -1;
            int vehicleId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(sqlGetIds)) {
                stmt.setInt(1, orderId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        deliveryId = rs.getInt("delivery_id");
                        vehicleId = rs.getInt("vehicle_id");
                    } else {
                        throw new SQLException("Shipping record not found for Order ID: " + orderId);
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOrder)) {
                stmt.setInt(1, orderId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateDelivery)) {
                stmt.setInt(1, deliveryId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateVehicle)) {
                stmt.setInt(1, vehicleId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(null, "Error completing order: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    public boolean restockShippment(int productID, int supplierID, int vehicleID, int quantity, int cost, String shippingDateTime) {
    	String sqlInsertDeliveryInfo = "INSERT INTO Delivery_Info (vehicle_id, shipping_datetime, arrival_datetime, status)\r\n"
    			+ "VALUES"
    			+ "(?, ?, NULL, 'Shipping')";
   
    	String sqlInsertSupplierShipment = "INSERT INTO Supplier_Shipment (product_id, supplier_id, product_cost, quantity, delivery_id)\r\n"
    			+ "VALUES"
    			+ "(?, ?, ?, ?, ?)";
    	
    	String sqlUpdateVehicle = "UPDATE Vehicles\r\n"
    			+ "SET status = 'Occupied'\r\n"
    			+ "WHERE vehicle_id = ?";
    	
    	int deliveryInfoID = -1;
    	
    	try(Connection conn = db.getConnection()){
    		conn.setAutoCommit(false);
    		
    		try(PreparedStatement stmtDelivery = conn.prepareStatement(sqlInsertDeliveryInfo, PreparedStatement.RETURN_GENERATED_KEYS)){
    			stmtDelivery.setInt(1, vehicleID);
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
    		
    		try(PreparedStatement stmtVehicle = conn.prepareStatement(sqlUpdateVehicle)){
    			stmtVehicle.setInt(1, vehicleID);
    			int affectedRows = stmtVehicle.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Vehicle status update failed (0 rows affected).");
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
                    return false; 
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean restockArrivalUpdate(int supplierShipmentID, String arrivalDateTime) {
    	String sqlSupplierShipment = "SELECT ss.delivery_id, ss.product_id, ss.quantity, di.vehicle_id\r\n"
    			+ "FROM Supplier_Shipment ss\r\n"
    			+ "JOIN Delivery_Info di\r\n"
    			+ "ON di.delivery_id = ss.delivery_id\r\n"
    			+ "WHERE supplier_shipment_id = ?";
    	    	
    	String sqlDelivery = "UPDATE Delivery_Info \r\n"
    			+ "SET arrival_datetime = ?, status = 'Complete'\r\n"
    			+ "WHERE delivery_id = ?";
    	
    	String sqlUpdateProductStocks = "UPDATE Products \r\n"
    			+ "SET quantity = quantity + ? \r\n"
    			+ "WHERE product_id = ?";
    	
    	String sqlUpdateVehicle = "UPDATE Vehicles\r\n"
    			+ "SET status = 'Available'\r\n"
    			+ "WHERE vehicle_id = ?";
        
        String sqlCheckDate = "SELECT shipping_datetime FROM Delivery_Info WHERE delivery_id = ?";
    	
    	int deliveryInfoID = -1;
    	int productID = -1;
    	int quantity = 0;
    	int vehicleID = -1;
    	
    	try(Connection conn = db.getConnection()){
    		conn.setAutoCommit(false);
    		
    		try(PreparedStatement stmtSupplier = conn.prepareStatement(sqlSupplierShipment)){
                stmtSupplier.setInt(1, supplierShipmentID);
        		try(ResultSet rs = stmtSupplier.executeQuery()){
        			if(rs.next()) {
        				deliveryInfoID = rs.getInt("delivery_id");
        				productID = rs.getInt("product_id");
        				quantity = rs.getInt("quantity");
        				vehicleID = rs.getInt("vehicle_id");
        			}
        			else {
        				conn.rollback();
        				System.err.println("Failed to retrieve shipment details for supplier_shipment_id: " + supplierShipmentID);
        				return false;
        			}
        		}
    		}
    		
            try(PreparedStatement checkStmt = conn.prepareStatement(sqlCheckDate)){
                checkStmt.setInt(1, deliveryInfoID);
                ResultSet rsDate = checkStmt.executeQuery();
                if (rsDate.next()) {
                    Timestamp shippingTs = rsDate.getTimestamp("shipping_datetime");
                    if (shippingTs != null) { // Should not be null, but safe check
                        LocalDateTime shippingDate = shippingTs.toLocalDateTime();
                        LocalDateTime arrivalDate = LocalDateTime.parse(arrivalDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        if (!shippingDate.isBefore(arrivalDate)) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(null, "Violation of Business Rule 8: Shipping datetime ("+shippingDate+") must be strictly before arrival datetime ("+arrivalDate+").", "Business Rule Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            }

    		// If validation passes, update Delivery Info
    		try(PreparedStatement stmtDelivery = conn.prepareStatement(sqlDelivery)){
    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime ldt = LocalDateTime.parse(arrivalDateTime, formatter);
                Timestamp timestamp = Timestamp.valueOf(ldt);
                
                stmtDelivery.setTimestamp(1, timestamp);
                stmtDelivery.setInt(2, deliveryInfoID);

                int affectedRows = stmtDelivery.executeUpdate(); 
                if(affectedRows == 0) {
                    conn.rollback();
                    System.err.println("Arrival Date Time update failed (0 rows affected).");
                    return false;
                }
    		}
	
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
    		
    		try(PreparedStatement stmtVehicle = conn.prepareStatement(sqlUpdateVehicle)){
    			stmtVehicle.setInt(1, vehicleID);
    			int affectedRows = stmtVehicle.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Vehicle status update failed (0 rows affected).");
    				return false;
    			}
    		}
    		conn.commit();
    		return true;
    	}catch(SQLException e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    	}catch(Exception e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Formatting or unexpected error: " + e.getMessage(), "Application Error", JOptionPane.ERROR_MESSAGE);
    	}
    	return false;
    }
    
    public LocalDateTime getShippingTimestamp(int supplierShipmentID) {
        String sql = "SELECT di.shipping_datetime FROM Delivery_Info di " +
                     "JOIN Supplier_Shipment ss ON di.delivery_id = ss.delivery_id " +
                     "WHERE ss.supplier_shipment_id = ?";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierShipmentID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("shipping_datetime");
                    if (ts != null) {
                        return ts.toLocalDateTime();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    public boolean restockShipment(int productID, int supplierID, int vehicleID, int quantity, int cost, String shippingDateTime) {

    	String sqlInsertDeliveryInfo = "INSERT INTO Delivery_Info (vehicle_id, shipping_datetime, arrival_datetime, status)\r\n"
    			+ "VALUES"
    			+ "(?, ?, NULL, 'Shipping')";
   
    	String sqlInsertSupplierShipment = "INSERT INTO Supplier_Shipment (product_id, supplier_id, product_cost, quantity, delivery_id)\r\n"
    			+ "VALUES"
    			+ "(?, ?, ?, ?, ?)";
    	
    	String sqlUpdateVehicle = "UPDATE Vehicles\r\n"
    			+ "SET status = 'Occupied'\r\n"
    			+ "WHERE vehicle_id = ?";
    	
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
    		
    		//Update vehicle to occupied
    		try(PreparedStatement stmtVehicle = conn.prepareStatement(sqlUpdateVehicle)){
    			stmtVehicle.setInt(1, vehicleID);

    			
    			int affectedRows = stmtVehicle.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Vehicle status update failed (0 rows affected).");
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
    	String sqlSupplierShipment = "SELECT ss.delivery_id, ss.product_id, ss.quantity, di.vehicle_id\r\n"
    			+ "FROM Supplier_Shipment ss\r\n"
    			+ "JOIN Delivery_Info di\r\n"
    			+ "ON di.delivery_id = ss.delivery_id\r\n"
    			+ "WHERE supplier_shipment_id = ?";
    	    	
    	String sqlDelivery = "UPDATE Delivery_Info \r\n"
    			+ "SET arrival_datetime = ?, status = 'Complete'\r\n"
    			+ "WHERE delivery_id = ?";
    	
    	String sqlUpdateProductStocks = "UPDATE Products \r\n"
    			+ "SET quantity = quantity + ? \r\n"
    			+ "WHERE product_id = ?";
    	
    	String sqlUpdateVehicle = "UPDATE Vehicles\r\n"
    			+ "SET status = 'Available'\r\n"
    			+ "WHERE vehicle_id = ?";
    	
    	int deliveryInfoID = -1;
    	int productID = -1;
    	int quantity = 0;
    	int vehicleID = -1;
    	
    	try(Connection conn = db.getConnection()){
    		conn.setAutoCommit(false);
    		
    		try(PreparedStatement stmtSupplier = conn.prepareStatement(sqlSupplierShipment)){
                stmtSupplier.setInt(1, supplierShipmentID);

    			//retrieve delivery_id, product_id, quantitiy, and vehicle_id
        		try(ResultSet rs = stmtSupplier.executeQuery()){
        			if(rs.next()) {
        				deliveryInfoID = rs.getInt("delivery_id");
        				productID = rs.getInt("product_id");
        				quantity = rs.getInt("quantity");
        				vehicleID = rs.getInt("vehicle_id");
        			}
        			else {
        				conn.rollback();
        				System.err.println("Failed to retrieve shipment details for supplier_shipment_id: " + supplierShipmentID);
        				return false;
        			}
        		}
    			
    		}
    		
    		//insert address of supplier
    		try(PreparedStatement stmtDelivery = conn.prepareStatement(sqlDelivery)){
    			
    			//convert the string into timestamp (ito ung datetime ng sql)
    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime ldt = LocalDateTime.parse(arrivalDateTime, formatter);
                Timestamp timestamp = Timestamp.valueOf(ldt);
                
                stmtDelivery.setTimestamp(1, timestamp);
                stmtDelivery.setInt(2, deliveryInfoID);

                int affectedRows = stmtDelivery.executeUpdate(); 
                
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
    		
    		//Update vehicle to occupied
    		try(PreparedStatement stmtVehicle = conn.prepareStatement(sqlUpdateVehicle)){
    			stmtVehicle.setInt(1, vehicleID);
	
    			int affectedRows = stmtVehicle.executeUpdate();
    			if(affectedRows == 0) {
    				conn.rollback();
    				System.err.println("Vehicle status update failed (0 rows affected).");
    				return false;
    			}
    			
    		}
    		
    		conn.commit();
    		return true;

    		
    	}catch(SQLException e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    	}catch(Exception e) {
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Formatting or unexpected error: " + e.getMessage(), "Application Error", JOptionPane.ERROR_MESSAGE);
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
        String sql = "SELECT SUM(total_price) FROM Orders WHERE MONTH(order_datetime) = ? AND YEAR(order_datetime) = ? AND status = 'Completed';";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, month);
            stmt.setString(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.00;
    }
    
    public int getTotalOrder(String month, String year) throws SQLException{
        String sql = "SELECT COUNT(order_id) FROM Orders WHERE MONTH(order_datetime) = ? AND YEAR(order_datetime) = ? AND status = 'Completed';";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, month);
            stmt.setString(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public DefaultTableModel getProductRefundsReport(String month, String year) throws SQLException {
        String sql = "SELECT r.return_reason AS 'Reason', COUNT(r.return_id) AS 'Count', " +
                     "SUM(r.product_quantity) AS 'Total Quantity Returned' " +
                     "FROM Returns r " +
                     "WHERE MONTH(r.return_date) = ? AND YEAR(r.return_date) = ? " +
                     "GROUP BY r.return_reason";
        
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, month);
            stmt.setString(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                return buildTableModel(rs);
            }
        }
    }
    
    public DefaultTableModel getInventoryTrackingReport(String month, String year) throws SQLException {
        String sql = "SELECT p.name AS 'Product Name', " +
                     "COALESCE(SUM(DISTINCT CASE WHEN MONTH(o.order_datetime) = ? AND YEAR(o.order_datetime) = ? AND o.status = 'Completed' THEN od.quantity ELSE 0 END), 0) AS 'Sold (Month)', " +
                     "COALESCE(SUM(DISTINCT CASE WHEN MONTH(di.arrival_datetime) = ? AND YEAR(di.arrival_datetime) = ? AND di.status = 'Complete' THEN ss.quantity ELSE 0 END), 0) AS 'Replenished (Month)', " +
                     "p.quantity AS 'Current Stock Level' " +
                     "FROM Products p " +
                     "LEFT JOIN Order_Details od ON p.product_id = od.product_id " +
                     "LEFT JOIN Orders o ON od.order_id = o.order_id " +
                     "LEFT JOIN Supplier_Shipment ss ON p.product_id = ss.product_id " +
                     "LEFT JOIN Delivery_Info di ON ss.delivery_id = di.delivery_id " +
                     "GROUP BY p.product_id, p.name, p.quantity";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, month);
            stmt.setString(2, year);
            stmt.setString(3, month);
            stmt.setString(4, year);
            try (ResultSet rs = stmt.executeQuery()) {
                return buildTableModel(rs);
            }
        }
    }
    
    public DefaultTableModel getTop5SellingProducts(String year) throws SQLException {
        String sql = "SELECT p.name AS 'Product Name', " +
                     "SUM(od.quantity) AS 'Total Sold', " +
                     "FORMAT(SUM(od.unit_price * od.quantity), 2) AS 'Total Revenue' " +
                     "FROM Order_Details od " +
                     "JOIN Orders o ON od.order_id = o.order_id " +
                     "JOIN Products p ON od.product_id = p.product_id " +
                     "WHERE YEAR(o.order_datetime) = ? AND o.status = 'Completed' " +
                     "GROUP BY p.product_id, p.name " +
                     "ORDER BY SUM(od.quantity) DESC " +
                     "LIMIT 5";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                return buildTableModel(rs);
            }
        }
    }
    
 // Helper method to convert ResultSet to DefaultTableModel
    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        DefaultTableModel model = new DefaultTableModel();

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
    
    public boolean processReturn(int orderDetailId, int qty, String reason, boolean resellable) {
        String sqlGetDeliveryDate = 
              "SELECT di.arrival_datetime, od.product_id "
            + "FROM Order_Details od "
            + "JOIN Orders o ON od.order_id = o.order_id "
            + "JOIN Shipping s ON o.order_id = s.order_id "
            + "JOIN Delivery_Info di ON s.delivery_id = di.delivery_id "
            + "WHERE od.order_detail_id = ?";
        
        String sqlInsertReturn = "INSERT INTO Returns (order_detail_id, return_reason, product_quantity, return_date) VALUES (?, ?, ?, NOW())";
        String sqlUpdateProduct = "UPDATE Products SET quantity = quantity + ? WHERE product_id = ?";
        String sqlUpdateOrder = "UPDATE Orders o JOIN Order_Details od ON o.order_id = od.order_id SET o.status = 'Returned' WHERE od.order_detail_id = ?";

        Connection conn = null;

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            int productId = -1;
            
            // 1. Validation: Check 7 Days Rule
            try (PreparedStatement stmt = conn.prepareStatement(sqlGetDeliveryDate)) {
                stmt.setInt(1, orderDetailId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Timestamp arrivalTs = rs.getTimestamp("arrival_datetime");
                        productId = rs.getInt("product_id");
                        
                        if (arrivalTs == null) {
                            JOptionPane.showMessageDialog(null, "Cannot return: Order hasn't arrived yet.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                            conn.rollback();
                            return false;
                        }
                        
                        // Calculate difference in days
                        long diffInMillis = System.currentTimeMillis() - arrivalTs.getTime();
                        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
                        
                        if (diffInDays > 7) {
                            JOptionPane.showMessageDialog(null, "Return rejected: Order arrived more than 7 days ago (" + diffInDays + " days).", "Business Rule Violation", JOptionPane.WARNING_MESSAGE);
                            conn.rollback();
                            return false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Order Detail ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Insert into Returns
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertReturn)) {
                stmt.setInt(1, orderDetailId);
                stmt.setString(2, reason);
                stmt.setInt(3, qty);
                stmt.executeUpdate();
            }

            // 3. If resellable, update product quantity
            if (resellable && productId != -1) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateProduct)) {
                    stmt.setInt(1, qty);
                    stmt.setInt(2, productId);
                    stmt.executeUpdate();
                }
            }
            
            // 4. Update Order Status to Returned
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOrder)) {
                stmt.setInt(1, orderDetailId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    public LocalDateTime getShippingTimestamp(int supplierShipmentID) {
        String sql = "SELECT di.shipping_datetime FROM Delivery_Info di " +
                     "JOIN Supplier_Shipment ss ON di.delivery_id = ss.delivery_id " +
                     "WHERE ss.supplier_shipment_id = ?";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierShipmentID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("shipping_datetime");
                    if (ts != null) {
                        return ts.toLocalDateTime();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean isSupplierEmailUnique(String email) {
        String sql = "SELECT 1 FROM Suppliers WHERE email = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // Returns true if NO record found (Unique)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }

    public boolean isSupplierPhoneUnique(String phone) {
        String sql = "SELECT 1 FROM Suppliers WHERE phone_number = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // Returns true if NO record found (Unique)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createNewSupplier(String name, String phone, String email, String street, String city, String zipCode) {
        String sqlAddress = "INSERT INTO Addresses (street, city, zip_code) VALUES (?, ?, ?)";
        String sqlSupplier = "INSERT INTO Suppliers (company_name, phone_number, email, address_id) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmtAddr = null;
        PreparedStatement stmtSup = null;
        ResultSet generatedKeys = null;

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Address
            stmtAddr = conn.prepareStatement(sqlAddress, Statement.RETURN_GENERATED_KEYS);
            stmtAddr.setString(1, street);
            stmtAddr.setString(2, city);
            stmtAddr.setString(3, zipCode);
            
            int affectedRows = stmtAddr.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            // 2. Retrieve Address ID
            generatedKeys = stmtAddr.getGeneratedKeys();
            int addressId;
            if (generatedKeys.next()) {
                addressId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating address failed, no ID obtained.");
            }

            // 3. Insert Supplier
            stmtSup = conn.prepareStatement(sqlSupplier);
            stmtSup.setString(1, name);
            stmtSup.setString(2, phone);
            stmtSup.setString(3, email);
            stmtSup.setInt(4, addressId);
            
            stmtSup.executeUpdate();

            conn.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmtAddr != null) stmtAddr.close();
                if (stmtSup != null) stmtSup.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    

}
