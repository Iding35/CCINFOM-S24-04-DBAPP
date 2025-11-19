import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;


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
        view.setViewOrdersAction(e -> handleViewOrders()); 
        
        // Hook the Logout action
        view.setReturnToLoginAction(e -> handleLogout());
    }

    private void handleLogout() {
        // Close the current customer frame
        view.dispose();
        
        // Launch the Login MVC pair
        LoginView loginView = new LoginView();
        LoginModel loginModel = new LoginModel();
        new LoginController(loginView, loginModel);
    }


    private void loadProductList() {
        try {
            
            DefaultTableModel productsModel = model.getAllProducts(); 
            
            view.setProductTableModel(productsModel); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void handleViewCart() {
        try {
            DefaultTableModel cartModel = model.getCartItems(this.customerId);
            
            if (cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(view, "Your cart is empty!", "Cart", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JTable cartTable = new JTable(cartModel);
            
            // HIDE the product_id column (first column, index 0)
            cartTable.getColumnModel().getColumn(0).setMinWidth(0);
            cartTable.getColumnModel().getColumn(0).setMaxWidth(0);
            cartTable.getColumnModel().getColumn(0).setWidth(0);
            
            JPanel panel = new JPanel(new java.awt.BorderLayout());
            panel.add(new JScrollPane(cartTable), java.awt.BorderLayout.CENTER);
            
            // Button Panel (South) - Centered buttons for checkout/remove
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton checkoutBtn = new JButton("Checkout / Place Order");
            JButton removeBtn = new JButton("Remove Selected Item"); 
            
            // --- REMOVE ACTION (DELETE ENTIRE ROW) ---
            removeBtn.addActionListener(e -> {
                int selectedRow = cartTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(view, "Please select an item to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Get the Product ID from the hidden first column (index 0)
                int productIdToRemove = (int) cartTable.getValueAt(selectedRow, 0); 
                String productName = (String) cartTable.getValueAt(selectedRow, 1);
                
                // ASKING FOR CONFIRMATION TO REMOVE ALL
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Remove ALL instances of " + productName + " from cart?", 
                        "Confirm Removal (Full Item)", 
                        JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Call the simple DELETE method
                    if (model.removeFullItem(this.customerId, productIdToRemove)) {
                        JOptionPane.showMessageDialog(view, "Item " + productName + " removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Close and re-open the cart dialog to show the refreshed state
                        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(removeBtn);
                        if (w != null) w.setVisible(false);
                        handleViewCart();
                    } else {
                        JOptionPane.showMessageDialog(view, "Failed to remove item. Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // --- CHECKOUT ACTION ---
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
            
            buttonPanel.add(removeBtn);
            buttonPanel.add(checkoutBtn);
            
            panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(view, panel, "Your Shopping Cart", JOptionPane.PLAIN_MESSAGE);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading cart: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Handler for the "My Profile" button
    private void handleViewProfile() {
        try {
            // 1. Get Profile Details
            String profileDetails = model.getCustomerProfile(this.customerId);
            
            // 2. Assemble the view
            JTextArea profileArea = new JTextArea(profileDetails);
            profileArea.setEditable(false);
            
            // 3. Display the popup
            JOptionPane.showMessageDialog(
                view, 
                new JScrollPane(profileArea), 
                "My Profile (ID: " + this.customerId + ")", 
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading customer details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Handler for the "My Orders" button (Includes Return Logic)
    private void handleViewOrders() {
        try {
            // 1. Get Order History Data
            DefaultTableModel orderHistoryModel = model.getCustomerOrderHistory(this.customerId);
            
            if (orderHistoryModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(view, "You have no past orders.", "Order History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Table Setup
            JTable orderTable = new JTable(orderHistoryModel);
            orderTable.setEnabled(true); // Must allow selection for returns
            orderTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            
            // 2. Panel Setup
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            JScrollPane orderScrollPane = new JScrollPane(orderTable);
            panel.add(orderScrollPane, BorderLayout.CENTER);
            
            // 3. Button Panel (South)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton returnOrderButton = new JButton("Initiate Return for Selected Order");
            
            // 4. Button Action
            returnOrderButton.addActionListener(e -> {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(view, "Please select an order from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int orderId = (int) orderTable.getValueAt(selectedRow, 0); 
                String status = (String) orderTable.getValueAt(selectedRow, 2); 
                
                // BUSINESS RULE CHECK: Must be 'Completed' to return
                if (!"Completed".equalsIgnoreCase(status)) {
                    JOptionPane.showMessageDialog(view, 
                        "Order ID " + orderId + " cannot be returned. Status must be 'Completed'. (Current: " + status + ").", 
                        "Return Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Proceed to detailed return checks (7-day rule implementation is handled here)
                handleReturnCheck(orderId);
            });
            
            // Add only the primary action button to the panel
            buttonPanel.add(returnOrderButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            // 5. Display the popup
            JOptionPane.showMessageDialog(
                view, 
                panel, 
                "My Orders (" + orderHistoryModel.getRowCount() + " found)", 
                JOptionPane.PLAIN_MESSAGE
            );
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading order history: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper method for the final, detailed return check (placeholder for 7-day rule)
    private void handleReturnCheck(int orderId) {
        
        try {
            if (model.isPastReturnDeadline(orderId)) {
                JOptionPane.showMessageDialog(view, 
                    "Return window closed. Deadline expired 7 days after delivery (Delivery date is currently based on database link).", 
                    "Return Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Database error during date verification.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
        
        // --- Final Confirmation ---
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Order ID " + orderId + " is eligible for return.\nDo you wish to proceed?", 
            "Confirm Return", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Implement model.processReturn(orderId); here (Missing feature)
            JOptionPane.showMessageDialog(view, "Return processing initiated for Order ID " + orderId + ". (Model logic to create the transaction needs to be implemented.)", "Return Started", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(view, "Item added to cart! (Quantity +1)", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view, "Failed to add item. It might already be in your cart.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}