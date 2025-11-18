import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class AdminView extends JFrame {
    
    private final int PANEL_WIDTH = 1500;
    private final int PANEL_HEIGHT = 750; 
    
    //navigation buttons
    private JButton viewProductsButton;
    private JButton viewSuppliersButton;
    private JButton viewVehiclesButton;
    private JButton viewOrdersButton;
    private JButton viewSupplierShipmentButton;
    private JButton viewReportsButton;
    
    //update product
    private JTextField productIdField;
    private JComboBox<String> updateProductFieldDropdown;
    private JTextField updateProductValueField;
    private JComboBox<String> categoryDropdown;
    private JComboBox<String> statusDropdown;
    private JButton updateProductButton;
    
    //add new product
    private JTextField productNameField;
    private JTextField productDescriptionField;
    private JTextField productBrandField;
    private JTextField productPriceField;
    private JComboBox<String> newProductCategoryDropdown;
    private JComboBox<String> newProductStatusDropdown;
    private JButton addProductButton;
    
    //add new supplier
    
    //add new vehicle
    
    //update order status
    private JTextField orderIdField;
    private JComboBox<String> newStatusOptions;
    private JButton updateStatusButton;
    
    //restock product
    private JTextField restockProductIdField;
    private JTextField restockSupplierIdField;
    private JTextField restockVehicleIdField;
    private JTextField restockQuantityField;
    private JTextField restockCostField;
    private JButton shipButton;    
    
    //update restock arrival
    private JTextField restockSupplierShipmentIdField;
    private JButton updateArrivalButton;
    
    //REPORTS
    //reports - sales
    private JComboBox<String> reportTypeDropdown; 
    private JComboBox<String> salesMonth;
    private JComboBox<String> salesYear;
    private JButton salesReportButton; 
    
    //reports - output labels 
    private JLabel monthResult;
    private JLabel yearResult;
    private JLabel totalRevenueResult;
    private JLabel totalOrderResult;
    
    
    //panels
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
        viewSuppliersButton = new JButton("View Suppliers");
        viewVehiclesButton = new JButton("View Vehicles");
        viewSupplierShipmentButton = new JButton("View Supplier Shipments");
        viewReportsButton = new JButton("Sales Report");
        
        viewProductsButton.setBackground(Color.decode("#ADD1DB"));
        viewOrdersButton.setBackground(Color.decode("#ADD1DB"));
        viewSuppliersButton.setBackground(Color.decode("#ADD1DB"));
        viewVehiclesButton.setBackground(Color.decode("#ADD1DB"));
        viewSupplierShipmentButton.setBackground(Color.decode("#ADD1DB"));
        viewReportsButton.setBackground(Color.decode("#ADD1DB"));
        
        
        //FOR SALES REPORT
        this.monthResult = new JLabel("N/A");
        this.yearResult = new JLabel("N/A");
        this.totalRevenueResult = new JLabel("N/A");
        this.totalOrderResult = new JLabel("N/A");
        
        this.monthResult.setForeground(Color.WHITE);
        this.yearResult.setForeground(Color.WHITE);
        this.totalRevenueResult.setForeground(Color.WHITE);
        this.totalOrderResult.setForeground(Color.WHITE);
        ;
        navPanel.add(viewProductsButton);
        navPanel.add(viewSuppliersButton);
        navPanel.add(viewVehiclesButton);
        navPanel.add(viewOrdersButton);
        navPanel.add(viewSupplierShipmentButton);
        navPanel.add(viewReportsButton);
        
        northPanel.add(navPanel, BorderLayout.SOUTH);
        
        this.add(northPanel, BorderLayout.NORTH);
        
        //kung saan ung pang input ng admin
        eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBackground(Color.DARK_GRAY);
        eastPanel.setPreferredSize(new Dimension(400, 0)); 
        
        //buttons for eastPanel
        updateProductButton = new JButton("Update Product");
        addProductButton = new JButton("Add Product");
        //addSupplierButton = new JButton("Add Supplier");
        //addVehicleButton = new JButton("Add Vehicle");
        updateStatusButton = new JButton("Update Status");
        shipButton = new JButton("Ship Products");
        updateArrivalButton = new JButton("Arrived");
        salesReportButton = new JButton("Report");
        
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
    
    public void setReportData(String reportType) {
        centerPanel.removeAll();
        centerPanel.setBackground(Color.GRAY);
        
        JPanel contentPanel = null;
        
        if("SALES".equals(reportType)) {
            contentPanel = createSalesReportResultPanel();
        }
        
        if(contentPanel != null) {
            centerPanel.add(contentPanel, BorderLayout.CENTER);
        }
        refreshFrame();
    }
//    	}
//    	else if ("REFUNDS".equals(reportType)) {
//    		contentPanel = //addPanel()
//    	}
//    	else if ("INVENTORY".equals(reportType)) {
//    		contentPanel = //addPanel()
//    	}
//    	else if ("TOP5".equals(reportType)) {
//    		contentPanel = //addPanel()
    
    public JPanel createProductPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBackground(Color.DARK_GRAY);
    	panel.add(createNewProductPanel());
    	panel.add(createProductUpdatePanel());
    	panel.add(Box.createVerticalGlue());
    	
    	return panel;
    }
    
    public JPanel createSupplierPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBackground(Color.DARK_GRAY);
    	//panel.add(createNewSupplierPanel());
    	//panel.add(createUSupplierUpdatePanel());
    	panel.add(Box.createVerticalGlue());
    	return panel;
    }
    
    public JPanel createVehiclePanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBackground(Color.DARK_GRAY);
    	//panel.add(createNewVehiclePanel());
    	//panel.add(createVehicleUpdatePanel());
    	panel.add(Box.createVerticalGlue());
    	return panel;
    }
    
    public JPanel createOrderPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBackground(Color.DARK_GRAY);
    	panel.add(createOrderStatusPanel());
    	panel.add(Box.createVerticalGlue());
    	
    	return panel;
    }
    
    public JPanel createRestockPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBackground(Color.DARK_GRAY);
    	panel.add(createNewRestockPanel());
    	panel.add(createUpdateArrivalRestockPanel());
    	panel.add(Box.createVerticalGlue());
    	
    	return panel;
    }
    
    public JPanel createReportPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);
        panel.add(createReportInputPanel());
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    public JPanel createSalesReportResultPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel headerLabel = new JLabel("SALES REPORT SUMMARY");
        headerLabel.setFont(new Font("Veranda", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel monthLabel = new JLabel("Month:");
        JLabel yearLabel = new JLabel("Year:");
        JLabel revenueLabel = new JLabel("Revenue:");
        JLabel totalOrdersLabel = new JLabel("Total Orders:");
        
        monthLabel.setForeground(Color.WHITE);
        yearLabel.setForeground(Color.WHITE);
        revenueLabel.setForeground(Color.WHITE);
        totalOrdersLabel.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(headerLabel, gbc);
        
        gbc.gridwidth = 1; 
        gbc.gridx = 0; gbc.gridy = 1; panel.add(monthLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(monthResult, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(yearLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(yearResult, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(revenueLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(totalRevenueResult, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(totalOrdersLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(totalOrderResult, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; panel.add(Box.createVerticalStrut(10), gbc);
        
        return panel;
    }
    
    public void setEastPanelContent(String panelType) {
    	eastPanel.removeAll();
    	JPanel contentPanel = null;
    	contentPanel = createProductPanel();
    	
    	if("PRODUCT".equals(panelType)) {
    		contentPanel = createProductPanel();
    	}
//    	else if ("SUPPLIER".equals(panelType)) {
//    		contentPanel = createSupplierPanel();
//    	}
//    	else if ("VEHICLE".equals(panelType)) {
//    		contentPanel = createVehiclePanel();
//    	}
    	else if ("ORDER".equals(panelType)) {
    		contentPanel = createOrderPanel();
    	}
    	else if ("RESTOCK".equals(panelType)) {
    		contentPanel = createRestockPanel();
    	}
    	else if ("REPORT".equals(panelType)) {
    		contentPanel = createReportPanel();
    	}
    	
    	if(contentPanel != null) {
    		eastPanel.add(contentPanel, BorderLayout.CENTER);
    	}
    	
    	refreshFrame();
    }
    
    private JPanel createEastPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            title, 
            TitledBorder.CENTER, TitledBorder.TOP, 
            new Font("Veranda", Font.BOLD, 16), Color.WHITE
        ));
        panel.setBackground(Color.GRAY.darker());
        
        return panel;
    }
    
    
   
   private JComboBox<String> createCategoryDropdown() {
       String[] categories = {
           "CPU", "GPU", "Motherboard", "Memory (RAM)", "Storage Device"
       };
       return new JComboBox<>(categories);
   }
   
   private JComboBox<String> createStatusDropdown(){
	   String[] status = {"Active", "Inactive"};
	   return new JComboBox<>(status);
   }

    private JPanel createProductUpdatePanel() {
    	
        productIdField = new JTextField(15);
        updateProductValueField = new JTextField(15);
        
        String[] updatableFields = {
            "name", "description", "brand", "price", "quantity", "category", "status"
        };
        updateProductFieldDropdown = new JComboBox<>(updatableFields);
        
        //not visible until they are selected
        categoryDropdown = createCategoryDropdown();
        categoryDropdown.setVisible(false);
        
        statusDropdown = createStatusDropdown();
        statusDropdown.setVisible(false);
        
        
        JPanel panel = createEastPanel(" Product Details Update ");
        
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

        // Row 3: Input for category or status
        JPanel inputContainer = new JPanel(new CardLayout());
        inputContainer.add(updateProductValueField, "TEXT_FIELD");
        inputContainer.add(categoryDropdown, "CATEGORY_DROPDOWN");
        inputContainer.add(statusDropdown, "STATUS_DROPDOWN");
        
        ((CardLayout) inputContainer.getLayout()).show(inputContainer, "TEXT_FIELD");
        
        //if i select category magiging dropwdown ung options for it
        updateProductFieldDropdown.addActionListener(e -> {
            CardLayout cl = (CardLayout) (inputContainer.getLayout());
            String selectedField = (String) updateProductFieldDropdown.getSelectedItem();
            if ("category".equals(selectedField)) {
                cl.show(inputContainer, "CATEGORY_DROPDOWN");
            } else if("status".equals(selectedField)) {
            	cl.show(inputContainer, "STATUS_DROPDOWN");
            }
            else {
                cl.show(inputContainer, "TEXT_FIELD");
            }
        });

        gbc.gridx = 1; gbc.gridy = 2; panel.add(inputContainer, gbc);


        // Row 4: Button
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(updateProductButton, gbc);
        
        return panel;
   }
    
    private JPanel createNewProductPanel() {
        
        productNameField = new JTextField(15);
        productDescriptionField = new JTextField(15);
        productBrandField = new JTextField(15);
        productPriceField = new JTextField(15);
        newProductCategoryDropdown = createCategoryDropdown();        
        newProductStatusDropdown = createStatusDropdown();
        
        JPanel panel = createEastPanel(" Add New Product ");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        JLabel descriptionLabel = new JLabel("Description:");
        JLabel brandLabel = new JLabel("Brand:");
        JLabel priceLabel = new JLabel("Price:");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel statusLabel = new JLabel("Status:");

        nameLabel.setForeground(Color.WHITE);
        descriptionLabel.setForeground(Color.WHITE);
        brandLabel.setForeground(Color.WHITE);
        priceLabel.setForeground(Color.WHITE);
        categoryLabel.setForeground(Color.WHITE);
        statusLabel.setForeground(Color.WHITE);
        addProductButton.setBackground(Color.decode("#ADD1DB"));


        gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(productNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(descriptionLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(productDescriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(brandLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(productBrandField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panel.add(priceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(productPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(categoryLabel, gbc); 
        gbc.gridx = 1; gbc.gridy = 4; panel.add(newProductCategoryDropdown, gbc); 
 
        gbc.gridx = 0; gbc.gridy = 5; panel.add(statusLabel, gbc); 
        gbc.gridx = 1; gbc.gridy = 5; panel.add(newProductStatusDropdown, gbc); 
        
        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
        panel.add(addProductButton, gbc);
        
        return panel;
    }
    
   
    /*private JPanel createNewSupplierPanel()*/
    
    /*private JPanel createNewVehiclePanel()*/

    private JPanel createOrderStatusPanel() {
        orderIdField = new JTextField(15);
        String[] statuses = {"Pending", "Shipping", "Completed", "Returned"};
        newStatusOptions = new JComboBox<>(statuses); 
                
        JPanel panel = createEastPanel(" Update Order Status ");
        
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
    
    public JPanel createNewRestockPanel() {
    	JPanel panel = createEastPanel(" Record New Shipment/Restock ");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        restockProductIdField = new JTextField(15);
        restockSupplierIdField = new JTextField(15);
        restockVehicleIdField = new JTextField(15);
        restockQuantityField = new JTextField(15);
        restockCostField = new JTextField(15);

        JLabel productIDLabel = new JLabel("Product ID:");
        JLabel supplierIDLabel = new JLabel("Supplier ID:");
        JLabel vehicleIDLabel = new JLabel("Vehicle ID:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JLabel costPriceLabel = new JLabel("Cost Price:");
        
        productIDLabel.setForeground(Color.WHITE);
        supplierIDLabel.setForeground(Color.WHITE);
        vehicleIDLabel.setForeground(Color.WHITE);
        quantityLabel.setForeground(Color.WHITE);
        costPriceLabel.setForeground(Color.WHITE);
        
        shipButton.setBackground(Color.decode("#ADD1DB"));
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(productIDLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(restockProductIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(supplierIDLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(restockSupplierIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panel.add(vehicleIDLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(restockVehicleIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(quantityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(restockQuantityField, gbc);
       
        gbc.gridx = 0; gbc.gridy = 4; panel.add(costPriceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(restockCostField, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        panel.add(shipButton, gbc);
        
        return panel;
    }
    
    public JPanel createUpdateArrivalRestockPanel() {
    	
    	restockSupplierShipmentIdField = new JTextField(15);
		//restockArrivalDateField = new JTextField(15);
		
    	JPanel panel = createEastPanel(" Update Supplier Shipment Arrival Date ");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel idLabel = new JLabel("Supplier Shipment ID:");
		idLabel.setForeground(Color.WHITE);
		updateArrivalButton.setBackground(Color.decode("#ADD1DB"));
		
		gbc.gridx = 0; gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(restockSupplierShipmentIdField, gbc);
		
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(updateArrivalButton, gbc);
        
        return panel;
    }
    
  //FOR REPORT
    private JComboBox<String> createMonthDropdown() {

    	String[] months = IntStream.rangeClosed(1, 12)
    			.mapToObj(i -> String.format("%02d", i)) //para nakaformat mga single valued num into 01, 02,...
    			.toArray(String[]::new);
        return new JComboBox<>(months);
    }
    
    private JComboBox<String> createYearDropdown() {
    	int currentYear = LocalDate.now().getYear();
        String[] years = IntStream.rangeClosed(2000, currentYear)
                                 .mapToObj(String::valueOf)
                                 .toArray(String[]::new);
        return new JComboBox<>(years);
    }
    
    
    
    private JPanel createReportInputPanel() {
        
        String[] reportTypes = {"Sales Report", "Product Refunds Report", "Inventory Tracking Report", "Top 5 Sellable Products"};
        reportTypeDropdown = new JComboBox<>(reportTypes);
        
        salesMonth = createMonthDropdown();
        salesYear = createYearDropdown();
        
        JPanel panel = createEastPanel(" Generate Reports ");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel typeLabel = new JLabel("Report Type:");
        JLabel monthLabel = new JLabel("Month:");
        JLabel yearLabel = new JLabel("Year:");
        
        typeLabel.setForeground(Color.WHITE);
        monthLabel.setForeground(Color.WHITE);
        yearLabel.setForeground(Color.WHITE);
        salesReportButton.setBackground(Color.decode("#ADD1DB"));
        
        // Row 0: Type
        gbc.gridx = 0; gbc.gridy = 0; panel.add(typeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(reportTypeDropdown, gbc);
        
        // Row 1: Month
        gbc.gridx = 0; gbc.gridy = 1; panel.add(monthLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(salesMonth, gbc);
        
        // Row 2: Year
        gbc.gridx = 0; gbc.gridy = 2; panel.add(yearLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(salesYear, gbc);
        
        // Row 3: Button
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(salesReportButton, gbc);
        
        // Logic to hide Month if "Top 5" is selected (Yearly report)
        reportTypeDropdown.addActionListener(e -> {
            String selected = (String) reportTypeDropdown.getSelectedItem();
            if("Top 5 Sellable Products".equals(selected)) {
                monthLabel.setVisible(false);
                salesMonth.setVisible(false);
            } else {
                monthLabel.setVisible(true);
                salesMonth.setVisible(true);
            }
        });
        
        return panel;
        
    }
    
    // Action Listeners 
    //nav bar action listener 
    public void setViewProductsAction(ActionListener listener) {
        if (viewProductsButton != null) viewProductsButton.addActionListener(listener);
    }
    public void setViewSuppliersAction(ActionListener listener) {
        if (viewSuppliersButton != null) viewSuppliersButton.addActionListener(listener);
    }
    public void setViewOrdersAction(ActionListener listener) {
        if (viewOrdersButton != null) viewOrdersButton.addActionListener(listener);
    }
    public void setViewSupplierShipmentkAction(ActionListener listener) {
        if (viewSupplierShipmentButton != null) viewSupplierShipmentButton.addActionListener(listener);
    }
    public void setViewVehiclesAction(ActionListener listener) {
        if (viewVehiclesButton != null) viewVehiclesButton.addActionListener(listener);
    }
    public void setViewReportsAction(ActionListener listener) {
        if (viewReportsButton != null) viewReportsButton.addActionListener(listener);
    }
    
    public String getProductIdField() { return productIdField.getText(); }
    public String getUpdateProductFieldDropdown() { return (String) updateProductFieldDropdown.getSelectedItem(); }
    public String getUpdateProductValueField() { return updateProductValueField.getText(); }
    public String getCategoryDropdown(){ return (String) categoryDropdown.getSelectedItem(); }
    public String getStatusDropdown(){ return (String) statusDropdown.getSelectedItem(); }
    public String getOrderIdField() { return orderIdField.getText(); }
    public void setProductUpdateAction(ActionListener listener) { updateProductButton.addActionListener(listener); }
    
    public String getProductNameField() { return productNameField.getText(); }
    public String getProductDescriptionField() { return productDescriptionField.getText(); }
    public String getProductBrandField() { return productBrandField.getText(); }
    public float getProductPriceField() { return Float.parseFloat(productPriceField.getText()); }
    public String getNewProductCategoryDropdown() { return (String) newProductCategoryDropdown.getSelectedItem(); }
    public String getNewProductStatusDropdown() { return (String) newProductStatusDropdown.getSelectedItem(); }
    public void setAddProductAction(ActionListener listener) { addProductButton.addActionListener(listener); }
    
    public String getNewStatus() { return (String) newStatusOptions.getSelectedItem(); }
    public void setUpdateStatusAction(ActionListener listener) { if (updateStatusButton != null) updateStatusButton.addActionListener(listener); }
    
    public int getRestockProductId() { return Integer.parseInt(restockProductIdField.getText()); }
    public int getRestockSupplierId() { return Integer.parseInt(restockSupplierIdField.getText()); }
    public int getRestockVehicleId() { return Integer.parseInt(restockVehicleIdField.getText()); }
    public int getRestockQuantity() { return Integer.parseInt(restockQuantityField.getText()); }
    public int getRestockCost() { return Integer.parseInt(restockCostField.getText()); }
    public void setShipAction(ActionListener listener) { shipButton.addActionListener(listener); }
    
    public int getSupplierShipmentId() { return Integer.parseInt(restockSupplierShipmentIdField.getText()); }
    public void setUpdateArrivalAction(ActionListener listener) { updateArrivalButton.addActionListener(listener); }
    
    // REPORTS INPUT GETTERS
    public String getSelectedReportType() {
        return (String) reportTypeDropdown.getSelectedItem();
    }
    public String getSalesMonth() { return (String) salesMonth.getSelectedItem(); }
    public String getSalesYear() { return (String) salesYear.getSelectedItem(); }
    
    public void setMonthResult(String month) { monthResult.setText(month); }
    public void setYearResult(String year) { yearResult.setText(year); }
    public void setTotalRevenueResult(String revenue) { totalRevenueResult.setText(revenue); }
    public void setTotalOrderResult(String order) { totalOrderResult.setText(order); }
    public void setSalesReportAction(ActionListener listener) {
      if (salesReportButton != null)salesReportButton.addActionListener(listener);
    }
    
}
