import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CustomerView extends JFrame {
    
    private final int PANEL_WIDTH = 1250;
    private final int PANEL_HEIGHT = 600;
    
    private JTable productTable;
    private JButton viewCartButton;
    private JButton addToCartButton;
    private JButton viewProfileButton;
    private JButton viewOrdersButton; 
    private JButton returnToLoginButton; 

    public CustomerView() {
        super("JHardware - Customer");
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        init();
        
        setVisible(true);
    }
    
    public void init() {
        
        // FIX: Use an anonymous JTable subclass to make the table non-editable,
        // while ensuring the component itself remains enabled for selection.
        productTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable
            }
        };
        
        productTable.setFillsViewportHeight(true);
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        add(scrollPane, BorderLayout.CENTER);
        
        
        // --- NEW NAVIGATION STRUCTURE (Far Right Logout) ---
        
        // Main container for navigation buttons (BorderLayout allows left/right separation)
        JPanel northContainer = new JPanel(new BorderLayout());

        // Panel for LEFT-ALIGNED buttons
        JPanel leftNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Panel for RIGHT-ALIGNED buttons (Logout)
        JPanel rightLogoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        viewCartButton = new JButton("View Cart");
        addToCartButton = new JButton("Add Selected to Cart");
        viewProfileButton = new JButton("My Profile");
        viewOrdersButton = new JButton("My Orders"); 
        
        returnToLoginButton = new JButton("Return to Login"); 
        
        // Add buttons to LEFT panel
        leftNavPanel.add(viewCartButton);
        leftNavPanel.add(addToCartButton);
        leftNavPanel.add(viewProfileButton);
        leftNavPanel.add(viewOrdersButton); 
        
        // Add button to RIGHT panel
        rightLogoutPanel.add(returnToLoginButton);
        
        // Add the two sub-panels to the main North container
        northContainer.add(leftNavPanel, BorderLayout.WEST); 
        northContainer.add(rightLogoutPanel, BorderLayout.EAST);
        
        add(northContainer, BorderLayout.NORTH);
    }
    
    public void setProductTableModel(DefaultTableModel model) {
        productTable.setModel(model);
    }
    
    public int getSelectedProductId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            return -1; 
        }
        
        Object value = productTable.getValueAt(selectedRow, 0);
        
        try {
            if (value instanceof Integer) {
                return (int) value;
            } else {
                return Integer.parseInt(value.toString());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error retrieving product ID from table. Data format invalid.", 
                "Data Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return -1;
        }
    }
    
    // ----------------------------------------------------
    // ACTION LISTENERS
    // ----------------------------------------------------

    public void setViewCartAction(ActionListener listener) {
        viewCartButton.addActionListener(listener);
    }

    public void setAddToCartAction(ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }

    public void setViewProfileAction(ActionListener listener) {
        viewProfileButton.addActionListener(listener);
    }
    
    public void setViewOrdersAction(ActionListener listener) {
        if (viewOrdersButton != null) viewOrdersButton.addActionListener(listener);
    }
    
    public void setReturnToLoginAction(ActionListener listener) {
        if (returnToLoginButton != null) returnToLoginButton.addActionListener(listener);
    }
    
}