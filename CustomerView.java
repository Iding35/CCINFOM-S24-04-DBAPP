import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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
        
        productTable = new JTable();
        productTable.setFillsViewportHeight(true);
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        add(scrollPane, BorderLayout.CENTER);
        
        
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewCartButton = new JButton("View Cart");
        addToCartButton = new JButton("Add Selected to Cart");
        viewProfileButton = new JButton("My Profile");
        
        northPanel.add(viewCartButton);
        northPanel.add(addToCartButton);
        northPanel.add(viewProfileButton);
        
        add(northPanel, BorderLayout.NORTH);
    }
    
 
    public void setProductTableModel(DefaultTableModel model) {
        productTable.setModel(model);
    }
    

    
    public void setViewCartAction(java.awt.event.ActionListener listener) {
        viewCartButton.addActionListener(listener);
    }

    public void setAddToCartAction(java.awt.event.ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }

    public void setViewProfileAction(java.awt.event.ActionListener listener) {
        viewProfileButton.addActionListener(listener);
    }


    public int getSelectedProductId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            // Assuming 'product_id' is the FIRST column (index 0)
            Object value = productTable.getValueAt(selectedRow, 0);
            return (int) value;
        }
        return -1; // No row selected
    }
    
}