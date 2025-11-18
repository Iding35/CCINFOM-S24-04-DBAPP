import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
        // 1. Load the initial list of products
        loadProductList();

        // 2. Set up action listeners
        view.setAddToCartAction(e -> handleAddToCart());
        view.setViewCartAction(e -> handleViewCart());
        
        // UN-COMMENT THIS LINE NOW:
        view.setViewProfileAction(e -> handleViewProfile()); 
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


    /**
     * Action for the "View Cart" button.
     * Displays the cart in a popup and allows the user to Checkout.
     */
    private void handleViewCart() {
        try {
            // 1. Get the data from the model
            DefaultTableModel cartModel = model.getCartItems(this.customerId);
            
            // 2. Check if empty
            if (cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(view, "Your cart is empty!", "Cart", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 3. Create a read-only table for the popup
            JTable cartTable = new JTable(cartModel);
            cartTable.setEnabled(false); // User can't edit cells directly
            
            // 4. Create a panel to hold the Table AND the Checkout Button
            JPanel panel = new JPanel(new java.awt.BorderLayout());
            panel.add(new JScrollPane(cartTable), java.awt.BorderLayout.CENTER);
            
            // Create the Checkout Button
            JButton checkoutBtn = new JButton("Checkout / Place Order");
            
            // Add the Checkout Logic (The code we wrote in the previous step)
            checkoutBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Are you sure you want to place this order?", 
                        "Confirm Order", 
                        JOptionPane.YES_NO_OPTION);
                        
                if (confirm == JOptionPane.YES_OPTION) {
                    // Call the placeOrder method in the model
                    if (model.placeOrder(this.customerId)) {
                         JOptionPane.showMessageDialog(view, "Order placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                         
                         // IMPORTANT: Close the popup window manually (since it's a JOptionPane)
                         java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(checkoutBtn);
                         if (w != null) w.setVisible(false);
                         
                         // Refresh the main product list (in case stock changed)
                         loadProductList();
                    } else {
                         JOptionPane.showMessageDialog(view, "Failed to place order. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            panel.add(checkoutBtn, java.awt.BorderLayout.SOUTH);

            // 5. Show the popup
            JOptionPane.showMessageDialog(view, panel, "Your Shopping Cart", JOptionPane.PLAIN_MESSAGE);
            
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
    
    
    private void handleViewProfile() {
        String profileInfo = model.getCustomerProfile(this.customerId);
        
        if (profileInfo.isEmpty()) {
             JOptionPane.showMessageDialog(view, "Could not load profile.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
             JOptionPane.showMessageDialog(view, profileInfo, "My Profile", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}