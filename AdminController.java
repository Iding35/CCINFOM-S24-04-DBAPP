import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminController {
    private AdminView view;
    private AdminModel model;

    public AdminController(AdminView view, AdminModel model) {
        this.view = view;
        this.model = model;
           
        setupActionListeners();
        showProductListing();
    }

    private void setupActionListeners() {
    	//navigation
    	view.setViewProductsAction(e -> showProductListing());
    	view.setViewSuppliersAction(e -> showSupplierListing());
    	view.setViewVehiclesAction(e -> showVehicleListing());
    	view.setViewOrdersAction(e -> showOrderListing());
    	view.setViewSupplierShipmentkAction(e -> showSupplierShipmentListing());
    	view.setViewReportsAction(e -> showReportPanel());
    	
    	// NEW: Hook the Logout action
    	view.setReturnToLoginAction(e -> handleLogout());
    	
    	//east panel
        view.setProductUpdateAction(e -> handleProductUpdate());
        view.setAddProductAction(e -> handleNewProduct());
        
        view.setAddSupplierAction(e -> handleNewSupplier());
        view.setAddVehicleAction(e -> handleNewVehicle());
        view.setUpdateStatusAction(e -> handleOrderUpdateStatus());
        view.setShipAction(e -> handleRestock());
        view.setUpdateArrivalAction(e -> handleUpdateArrivalRestock());
        view.setSalesReportAction(e -> handleGenerateReport());
    }
    
    // NEW: Method to handle logout and return to the main Login screen
    private void handleLogout() {
        // Close the current Admin frame
        view.dispose();
        
        // Launch the Login MVC pair
        LoginView loginView = new LoginView();
        LoginModel loginModel = new LoginModel();
        new LoginController(loginView, loginModel);
    }
    
    private void showReportPanel() {
        view.setEastPanelContent("REPORT");
    }
   
    private void handleGenerateReport() {
        String reportType = view.getSelectedReportType();
        String month = view.getSalesMonth();
        String year = view.getSalesYear();
        
        // Validation
        if (year.isEmpty()) {
             JOptionPane.showMessageDialog(view, "Please select a year.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        try {
            switch (reportType) {
                case "Sales Report":
                    if(month.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Please select a month.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double totalRevenue = model.getTotalRevenue(month, year); 
                    int totalOrders = model.getTotalOrder(month, year);
                    
                    view.setMonthResult(month);
                    view.setYearResult(year);
                    String formattedRevenue = String.format("₱%,.2f", totalRevenue);
                    view.setTotalRevenueResult(formattedRevenue);
                    view.setTotalOrderResult(String.valueOf(totalOrders));
                    
                    // This method switches center panel to the grid layout for specific sales summary
                    view.setReportData("SALES"); 
                    break;

                case "Product Refunds Report":
                    if(month.isEmpty()) {
                         JOptionPane.showMessageDialog(view, "Please select a month.", "Input Error", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    view.displayTableData(model.getProductRefundsReport(month, year));
                    break;

                case "Inventory Tracking Report":
                    if(month.isEmpty()) {
                         JOptionPane.showMessageDialog(view, "Please select a month.", "Input Error", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    view.displayTableData(model.getInventoryTrackingReport(month, year));
                    break;

                case "Top 5 Sellable Products":
                    view.displayTableData(model.getTop5SellingProducts(year));
                    break;
            }
            
            JOptionPane.showMessageDialog(view, reportType + " generated successfully!", "Report Success", JOptionPane.INFORMATION_MESSAGE);

        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error generating report: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    
    private void showProductListing() {
    	view.setEastPanelContent("PRODUCT");
    	try {
    		view.displayTableData(model.getProductListing());
    	}
    	catch(SQLException ex) {
    		JOptionPane.showMessageDialog(view, "Error loading product list: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    		ex.printStackTrace();
    	}
    }
    
    private void showSupplierListing() {
    	view.setEastPanelContent("SUPPLIER");
    	try {
    		view.displayTableData(model.getSupplierListing());
    	}catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error loading supplier list: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showVehicleListing() {
    	view.setEastPanelContent("VEHICLE");
    	try {
    		view.displayTableData(model.getVehicleListing());
    	}catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error loading vehicle list: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showOrderListing() {
    	view.setEastPanelContent("ORDER");
        try {
            view.displayTableData(model.getOrderListing());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error loading order list: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showSupplierShipmentListing() {
    	view.setEastPanelContent("RESTOCK");
    	try {
    		view.displayTableData(model.getSupplierShipmentListing());
    	}
    	catch(SQLException ex) {
    		JOptionPane.showMessageDialog(view, "Error loading product list: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
    		ex.printStackTrace();
    	}
    }
    
    private void handleNewProduct(){
    	String name = view.getProductNameField();
    	String description = view.getProductDescriptionField();
    	String brand = view.getProductBrandField();
    	String category = view.getNewProductCategoryDropdown();
    	String status = view.getNewProductStatusDropdown();
    
    	Float price = null;
        try {
            price = view.getProductPriceField(); 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    	if(name.isEmpty() || description.isEmpty() || brand.isEmpty() ||
    			price == null || price <= 0 || category.isEmpty() || status.isEmpty()) {
    		JOptionPane.showMessageDialog(view, "Please fill in all fields, and ensure Price is valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
    	}
    	
    	if (model.createNewProduct(name, description, brand, price, category, status)) {
		    JOptionPane.showMessageDialog(view, "New product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
		    JOptionPane.showMessageDialog(view, "Product creation failed. Check required values.", "Error", JOptionPane.ERROR_MESSAGE);
		}
    	
    }
    
    private void handleNewSupplier() {
        String name = view.getSupplierName();
        String phone = view.getSupplierPhone();
        String email = view.getSupplierEmail();
        String street = view.getSupplierStreet();
        String city = view.getSupplierCity();
        String zip = view.getSupplierZip();
        
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || 
            street.isEmpty() || city.isEmpty() || zip.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!model.isSupplierEmailUnique(email)) {
            JOptionPane.showMessageDialog(view, "Email address already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!model.isSupplierPhoneUnique(phone)) {
            JOptionPane.showMessageDialog(view, "Phone number already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
     
        if (model.createNewSupplier(name, phone, email, street, city, zip)) {
            JOptionPane.showMessageDialog(view, "Supplier added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showSupplierListing();
        } else {
            JOptionPane.showMessageDialog(view, "Failed to add supplier.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleNewVehicle() {
        String plate = view.getVehiclePlate();
        String type = view.getVehicleType();
        String status = view.getVehicleStatus();

        // 1. Validate input
        if (plate.isEmpty() || type == null || status == null) {
            JOptionPane.showMessageDialog(view, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Check uniqueness
        if (!model.isValidPlate(plate)) {
            JOptionPane.showMessageDialog(view, "Plate number already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Insert
        if (model.createNewVehicle(plate, type, status)) {
            JOptionPane.showMessageDialog(view, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showVehicleListing(); // Refresh list
        } else {
            JOptionPane.showMessageDialog(view, "Failed to add vehicle.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleProductUpdate() {
    	String idText = view.getProductIdField();
        String field = view.getUpdateProductFieldDropdown();
        String newValue;
        
        if ("category".equals(field)) {
            newValue = (String) view.getCategoryDropdown();
        } 
        else if ("status".equals(field)) {
        	newValue = (String) view.getStatusDropdown();
        }
        else {
            newValue = view.getUpdateProductValueField();
        }
        
        if (idText.isEmpty() || newValue.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(idText);
            
            if (model.updateProductDetail(productId, field, newValue)) {
                JOptionPane.showMessageDialog(view, "Product ID " + productId + " successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Update failed. Check Product ID and values.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Product ID must be an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getCurrentDateTime() {
    	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	LocalDateTime currentDateTime = LocalDateTime.now();
    	String datetime = currentDateTime.format(dateFormat);
    	
    	return datetime;
    }
    
    private void handleRestock() {
    	Integer productID = null; 
    	Integer supplierID = null;
    	Integer vehicleID = null;
    	Integer quantity = null;
    	Integer cost = null;
    	
    	String shippingDateTime = getCurrentDateTime();
    	
    	try {
    		productID = view.getRestockProductId();
    		supplierID = view.getRestockSupplierId();
    		vehicleID = view.getRestockVehicleId();
    		quantity = view.getRestockQuantity();
    		cost = view.getRestockCost();
    	}catch(NumberFormatException e) {
    		JOptionPane.showMessageDialog(view, "All fields must be in a number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
    	}
        
        // CRITICAL BUSINESS RULE: Each shipment must contain at least one product.
        if (quantity == null || quantity <= 0) {
            JOptionPane.showMessageDialog(view, "Quantity must be greater than zero.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    	
    	if(!model.checkValidVehicle(vehicleID))  {
    		JOptionPane.showMessageDialog(view, "Vehicle ID provided is invalid or occupied.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
    	}
    	
    	if(productID == null || supplierID == null || vehicleID == null || cost == null) {
    		JOptionPane.showMessageDialog(view, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
    	}
        
    	if (model.restockShippment(productID, supplierID, vehicleID, quantity, cost, shippingDateTime)) {
		    JOptionPane.showMessageDialog(view, "Product is shipping!", "Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
		    JOptionPane.showMessageDialog(view, "Restock failed. Check required values or database connection.", "Error", JOptionPane.ERROR_MESSAGE);
		}
    }
    
	private void handleUpdateArrivalRestock() {
		
    	Integer supplierShipmentID = null;
    	String arrivalDateTime = getCurrentDateTime();
    	
    	try {
    		supplierShipmentID = view.getSupplierShipmentId();
    		
    	}catch(NumberFormatException e) {
    		JOptionPane.showMessageDialog(view, "Supplier Shipment ID must be in a number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
    	}
    	
    	if(model.isDeliveryComplete(supplierShipmentID)) {
    		JOptionPane.showMessageDialog(view, "The shipment has already been completed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
    	}

    	if(model.restockArrivalUpdate(supplierShipmentID, arrivalDateTime)) {
    		JOptionPane.showMessageDialog(view, "Arrival DateTime is updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
		    JOptionPane.showMessageDialog(view, "Arrival DateTime Update failed. Check required values.", "Error", JOptionPane.ERROR_MESSAGE);
		}
    	
    }

    private void handleOrderUpdateStatus() {
        String idText = view.getOrderIdField();
        String newStatus = view.getNewStatus();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter Order ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int orderId = Integer.parseInt(idText);
            
            if (model.updateOrderStatus(orderId, newStatus)) {
                JOptionPane.showMessageDialog(view, "Status updated successfully for Order ID: " + orderId + " to " + newStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                JOptionPane.showMessageDialog(view, "Update failed. Order ID " + orderId + " not found or no change made.", "Update Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Order ID must be a valid whole number.", "Input Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Database Error: Could not update status.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    
}