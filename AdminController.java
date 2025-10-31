import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.InputMismatchException;

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
    	view.setViewProductsAction(e -> showProductListing());
    	view.setViewOrdersAction(e -> showOrderListing());
    	view.setRestockAction(e -> showRestock());
    	
        view.setProductUpdateAction(e -> handleProductUpdate());
        view.setUpdateStatusAction(e -> handleOrderUpdateStatus());
        view.setRecordRestockAction(e -> handleRestock());
    }
    
    private void showRestock() {
    	view.setEastPanelContent("RESTOCK");
    	try {
    		view.displayTableData(model.getProductSupplierListing());
    	}
    	catch(SQLException ex) {
    		JOptionPane.showMessageDialog(view, "Error loading product list: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
    		ex.printStackTrace();
    	}
    }
    
    private void showProductListing() {
    	view.setEastPanelContent("UPDATE");
    	try {
    		view.displayTableData(model.getProductListing());
    	}
    	catch(SQLException ex) {
    		JOptionPane.showMessageDialog(view, "Error loading product list: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
    		ex.printStackTrace();
    	}
    }
    
    private void showOrderListing() {
    	view.setEastPanelContent("UPDATE");
        try {
            view.displayTableData(model.getOrderListing());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error loading order list: " + ex.getMessage(), 
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void handleRestock() {
    	
    }

    private void handleProductUpdate() {
    	String idText = view.getProductIdField();
        String field = view.getUpdateProductFieldDropdown();
        String newValue;
        
        if ("category".equals(field)) {
            newValue = (String) view.getCategoryDropdown();
        } else {
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