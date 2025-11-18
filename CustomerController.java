import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.awt.FlowLayout;


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
     * Displays the cart in a popup and allows the user to Checkout or Remove items.
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
            
            // HIDE the product_id column (first column, index 0)
            cartTable.getColumnModel().getColumn(0).setMinWidth(0);
            cartTable.getColumnModel().getColumn(0).setMaxWidth(0);
            cartTable.getColumnModel().getColumn(0).setWidth(0);
            
            // 4. Create UI components for the dialog
            JPanel panel = new JPanel(new java.awt.BorderLayout());
            panel.add(new JScrollPane(cartTable), java.awt.BorderLayout.CENTER);
            
            // Button Panel (South)
            // FIX: Changed FlowLayout.RIGHT to FlowLayout.CENTER to center the buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            // Create buttons
            JButton checkoutBtn = new JButton("Checkout / Place Order");
            JButton removeBtn = new JButton("Remove Selected Item");
            
            // 5. Add Button Listeners
            
            // --- REMOVE ACTION ---
            removeBtn.addActionListener(e -> {
                int selectedRow = cartTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(view, "Please select an item to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Get the Product ID from the hidden first column (index 0)
                int productIdToRemove = (int) cartTable.getValueAt(selectedRow, 0); 
                String productName = (String) cartTable.getValueAt(selectedRow, 1); // Get name for confirmation
                
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Are you sure you want to remove " + productName + " from your cart?", 
                        "Confirm Removal", 
                        JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (model.removeFromCart(this.customerId, productIdToRemove)) {
                        JOptionPane.showMessageDialog(view, productName + " removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        // IMPORTANT: Manually close the existing dialog
                        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(removeBtn);
                        if (w != null) w.setVisible(false);
                        
                        // Re-open the cart dialog to show the refreshed state
                        handleViewCart();
                    } else {
                        JOptionPane.showMessageDialog(view, "Failed to remove item. Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // --- CHECKOUT ACTION (Logic retained) ---
            checkoutBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Are you sure you want to place this order?", 
                        "Confirm Order", 
                        JOptionPane.YES_NO_OPTION);
                        
                if (confirm == JOptionPane.YES_OPTION) {
                    if (model.placeOrder(this.customerId)) {
                         JOptionPane.showMessageDialog(view, "Order placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                         
                         java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(checkoutBtn);
                         if (w != null) w.setVisible(false);
                         
                         loadProductList();
                    } else {
                         JOptionPane.showMessageDialog(view, "Failed to place order. Cart might be empty or stock insufficient.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // 6. Assemble the button panel
            buttonPanel.add(removeBtn);
            buttonPanel.add(checkoutBtn);
            
            panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

            // 7. Show the popup
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