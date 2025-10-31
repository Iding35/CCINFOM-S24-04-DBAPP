import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class AdminView extends JFrame {
    
    private final int PANEL_WIDTH = 1350;
    private final int PANEL_HEIGHT = 600; 
    
    private JTextField productIdField;
    private JComboBox<String> updateProductFieldDropdown;
    private JTextField updateProductValueField;
    private JComboBox<String> categoryDropdown;
    private JButton updateProductButton;

    private JTextField orderIdField;
    private JComboBox<String> newStatusOptions;
    private JButton updateStatusButton;
    
    //nav buttons
    private JButton viewProductsButton;
    private JButton viewOrdersButton;
    private JButton restockButton;
    private JButton addProductsButton;
    private JButton addSuppliersButton;
    
    private JTextField restockProductIdField;
    private JTextField restockSupplierIdField;
    private JTextField restockQuantityField;
    private JTextField restockCostField;
    private JTextField restockOrderDateField;
    private JTextField restockArrivalDateField;
    private JButton recordRestockButton;
    
    private JPanel centerPanel;
    private JPanel eastPanel;
   
    
    public AdminView() {
        super("JHardware Admin - Stock & Order Management");
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        init();
        setVisible(true);
    }
    
    private void refreshFrame() {
        revalidate();
        repaint();
    }
    
    private void init() {
        getContentPane().removeAll();
        
        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel northLabel = new JLabel("JHardware (Admin)", SwingConstants.CENTER);
        northPanel.setBackground(Color.BLACK);
        northLabel.setForeground(Color.WHITE);
        northLabel.setFont(new Font("Veranda", Font.BOLD, 30));
        northPanel.add(northLabel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setBackground(Color.BLACK);
        
        viewProductsButton = new JButton("View Products");
        viewOrdersButton = new JButton("View Orders");
        viewProductsButton.setBackground(Color.decode("#ADD1DB"));
        viewOrdersButton.setBackground(Color.decode("#ADD1DB"));
        restockButton = new JButton("Restock Products");
        restockButton.setBackground(Color.decode("#ADDBD1"));
        
        navPanel.add(viewProductsButton);
        navPanel.add(viewOrdersButton);
        navPanel.add(restockButton);
        
        northPanel.add(navPanel, BorderLayout.SOUTH);
        
        this.add(northPanel, BorderLayout.NORTH);
        
        //kung saan ung pang input ng admin
        eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBackground(Color.DARK_GRAY);
        eastPanel.setPreferredSize(new Dimension(400, 0)); 

        this.add(eastPanel, BorderLayout.EAST);
        
        
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.GRAY);

        this.add(centerPanel, BorderLayout.CENTER);
        
        
        refreshFrame();
    }
    
    public void displayTableData(DefaultTableModel model) {

    	centerPanel.removeAll();
    	centerPanel.setBackground(Color.GRAY);
    	JTable table = new JTable(model);
    	JScrollPane scrollPane = new JScrollPane(table);
    	centerPanel.add(scrollPane, BorderLayout.CENTER);

    	refreshFrame();

    }
    
    private JPanel createUpdatePanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBackground(Color.DARK_GRAY);
    	panel.add(createProductUpdatePanel());
    	panel.add(createOrderStatusPanel());
    	panel.add(Box.createVerticalGlue());
    	
    	return panel;
    }
    
    public void setEastPanelContent(String panelType) {
    	eastPanel.removeAll();
    	JPanel contentPanel = null;
    	
    	if("UPDATE".equals(panelType)) {
    		contentPanel = createUpdatePanel();
    	}
    	else if ("RESTOCK".equals(panelType)) {
    		contentPanel = createRestockPanel();
    	}
    	
    	if(contentPanel != null) {
    		eastPanel.add(contentPanel, BorderLayout.CENTER);
    	}
    	
    	refreshFrame();
    }
    
    public JPanel createRestockPanel() {
    	JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            " Record New Shipment/Restock ", 
            TitledBorder.CENTER, TitledBorder.TOP, 
            new Font("Veranda", Font.BOLD, 16), Color.WHITE
        ));
        panel.setBackground(Color.GRAY.darker());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        restockProductIdField = new JTextField(15);
        restockSupplierIdField = new JTextField(15);
        restockQuantityField = new JTextField(15);
        restockCostField = new JTextField(15);
        restockOrderDateField = new JTextField(15);
        restockArrivalDateField = new JTextField(15);
        recordRestockButton = new JButton("Restock");

        JLabel[] labels = {
            new JLabel("Product ID:"), new JLabel("Supplier ID:"), new JLabel("Quantity:"),
            new JLabel("Cost Price:"), new JLabel("Order Date (YYYY-MM-DD HH:MM:SS):"), 
            new JLabel("Arrival Date (YYYY-MM-DD HH:MM:SS):")
        };
        
        for (JLabel label : labels) {
            label.setForeground(Color.WHITE);
        }
        recordRestockButton.setBackground(Color.decode("#ADD1DB"));

        JTextField[] fields = {
        		restockProductIdField, restockSupplierIdField, restockQuantityField,
        		restockCostField, restockOrderDateField, restockArrivalDateField
        };

        // Layout (six rows for data)
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; panel.add(labels[i], gbc);
            gbc.gridx = 1; gbc.gridy = i; panel.add(fields[i], gbc);
        }

        // Button Row
        gbc.gridx = 1; gbc.gridy = labels.length; gbc.anchor = GridBagConstraints.EAST;
        panel.add(recordRestockButton, gbc);
        
        return panel;
    }
    
    private JPanel createProductUpdatePanel() {
    	
        productIdField = new JTextField(15);
        updateProductValueField = new JTextField(15);
        updateProductButton = new JButton("Update Product");
        
        String[] updatableFields = {
            "name", "description", "brand", "price", "quantity", "category"
        };
        updateProductFieldDropdown = new JComboBox<>(updatableFields);
        
        String[] categories = {
        	"CPU", "GPU", "Motherboard", "Memory (RAM)", "Storage Device"
        };
        categoryDropdown = new JComboBox<>(categories);
        categoryDropdown.setVisible(false);
        
        
        JPanel panel = new JPanel(new GridBagLayout());
        
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            " Product Details Update ", TitledBorder.CENTER, TitledBorder.TOP, 
            new Font("Veranda", Font.BOLD, 16), Color.WHITE
        ));
        panel.setBackground(Color.GRAY.darker());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        JLabel idLabel = new JLabel("Product ID:");
        JLabel fieldLabel = new JLabel("Field to Update:");
        JLabel valueLabel = new JLabel("New Value:");

        idLabel.setForeground(Color.WHITE);
        fieldLabel.setForeground(Color.WHITE);
        valueLabel.setForeground(Color.WHITE);
        updateProductButton.setBackground(Color.decode("#ADD1DB"));


        // Row 1: Product ID
        gbc.gridx = 0; gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(productIdField, gbc);

        // Row 2: Field Selection Dropdown
        gbc.gridx = 0; gbc.gridy = 1; panel.add(fieldLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(updateProductFieldDropdown, gbc);

        // Row 3: New Value Label
        gbc.gridx = 0; gbc.gridy = 2; panel.add(valueLabel, gbc);

        // Row 3: Input for category
        JPanel inputContainer = new JPanel(new CardLayout());
        inputContainer.add(updateProductValueField, "TEXT_FIELD");
        inputContainer.add(categoryDropdown, "CATEGORY_DROPDOWN");
        
        ((CardLayout) inputContainer.getLayout()).show(inputContainer, "TEXT_FIELD");
        
        //if i select category magiging dropwdown ung options for it
        updateProductFieldDropdown.addActionListener(e -> {
            CardLayout cl = (CardLayout) (inputContainer.getLayout());
            String selectedField = (String) updateProductFieldDropdown.getSelectedItem();
            if ("category".equals(selectedField)) {
                cl.show(inputContainer, "CATEGORY_DROPDOWN");
            } else {
                cl.show(inputContainer, "TEXT_FIELD");
            }
        });

        gbc.gridx = 1; gbc.gridy = 2; panel.add(inputContainer, gbc);


        // Row 4: Button
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(updateProductButton, gbc);
        
        return panel;
   }

    private JPanel createOrderStatusPanel() {
        orderIdField = new JTextField(15);
        String[] statuses = {"Pending", "Shipping", "Completed", "Returned"};
        newStatusOptions = new JComboBox<>(statuses); 
        updateStatusButton = new JButton("Update Status");
        
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            " Order Status Update ", 
            TitledBorder.CENTER, TitledBorder.TOP, 
            new Font("Veranda", Font.BOLD, 16), Color.WHITE
        ));
        panel.setBackground(Color.GRAY.darker());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Order ID:");
        JLabel statusLabel = new JLabel("New Status:");
        
        idLabel.setForeground(Color.WHITE);
        statusLabel.setForeground(Color.WHITE);
        updateStatusButton.setBackground(Color.decode("#ADD1DB"));

        // Row 1: Order ID
        gbc.gridx = 0; gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(orderIdField, gbc);

        // Row 2: New Status (ComboBox)
        gbc.gridx = 0; gbc.gridy = 1; panel.add(statusLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(newStatusOptions, gbc);

        // Row 3: Button
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(updateStatusButton, gbc);
        
        return panel;
    }
    
    
    // Action Listeners
    public void setProductUpdateAction(ActionListener listener) {
    	updateProductButton.addActionListener(listener);
    }
    public void setUpdateStatusAction(ActionListener listener) {
        if (updateStatusButton != null) updateStatusButton.addActionListener(listener);
    }
    public void setViewProductsAction(ActionListener listener) {
        if (viewProductsButton != null) viewProductsButton.addActionListener(listener);
    }
    public void setViewOrdersAction(ActionListener listener) {
        if (viewOrdersButton != null) viewOrdersButton.addActionListener(listener);
    }
    public void setRestockAction(ActionListener listener) {
        if (restockButton != null) restockButton.addActionListener(listener);
    }
    public void setRecordRestockAction(ActionListener listener) {
    	recordRestockButton.addActionListener(listener);
    }
    
    public String getRestockProductId() {
    	return restockProductIdField.getText();
    }
    public String getRestockSupplierId() {
    	return restockSupplierIdField.getText();
    }
    public String getRestockQuantity() {
    	return restockQuantityField.getText();
    }
    public String getRestockOrderDate() {
    	return restockOrderDateField.getText();
    }
    public String getRestockArrivalDate() {
    	return restockArrivalDateField.getText();
    }
    public String getProductIdField() {
    	return productIdField.getText();
    }
    public String getUpdateProductFieldDropdown() {
    	return (String) updateProductFieldDropdown.getSelectedItem();
    }
    public String getUpdateProductValueField() {
    	return updateProductValueField.getText();
    }
    public String getCategoryDropdown(){
        return (String) categoryDropdown.getSelectedItem();
    }
    public String getOrderIdField() {
    	return orderIdField.getText();
    }
    public String getNewStatus() { return (String) newStatusOptions.getSelectedItem(); }
    public void resetTextField() {
    	
    }
}