import java.awt.BorderLayout;

import javax.swing.JFrame;

public class CustomerView extends JFrame{
	private final int PANEL_WIDTH = 1250;
    private final int PANEL_HEIGHT = 600; 
    
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
		
	}
}
