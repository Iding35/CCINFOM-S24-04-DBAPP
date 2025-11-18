import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;


public class CustomerController {
    
    private CustomerView view;
    private CustomerModel model;
    private int customerId; 


    public CustomerController(CustomerView view, CustomerModel model, int customerId) {
        this.view = view;
        this.model = model;
        this.customerId = customerId;


        view.setTitle("JHardware - Customer Portal (User ID: " + customerId + ")");

   
        initController();
    }

    private void initController() {
       
        loadProductList();

      
        view.setAddToCartAction(e -> handleAddToCart());
        view.setViewCartAction(e -> handleViewCart());
     
    }


    private void loadProductList() {
        try {
            
            DefaultTableModel productsModel = model.getAllProducts(); 
            
            // We'll update the view to have this method
            view.setProductTableModel(productsModel); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void handleViewCart() {
        try {
            DefaultTableModel cartModel = model.getCartItems(this.customerId);
            
       
            JTable cartTable = new JTable(cartModel);
            cartTable.setEnabled(false); 
            
      
            JOptionPane.showMessageDialog(view, 
                                        new JScrollPane(cartTable), 
                                        "Your Shopping Cart", 
                                        JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading cart: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

private void handleAddToCart() {
        
        int selectedProductId = view.getSelectedProductId();
        
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(view, "Please select a product from the table first.", "No Product Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

      
        int quantity = 1; 
        
        if (model.addToCart(this.customerId, selectedProductId, quantity)) {
            JOptionPane.showMessageDialog(view, "Item added to cart!");
        } else {
            JOptionPane.showMessageDialog(view, "Failed to add item. It might already be in your cart.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}