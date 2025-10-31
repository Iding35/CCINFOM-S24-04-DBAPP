import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LoginView extends JFrame{
	private final int PANEL_WIDTH = 500;
	private final int PANEL_HEIGHT = 500;
	
	private JTextField emailField;
	private JPasswordField passwordField;
	
	private JButton loginButton;
	private JButton adminButton;
	private JButton backButton;
	
	public LoginView() {
		super("JHardware");
		setSize(PANEL_WIDTH, PANEL_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		customerLogin();
		setVisible(true);
		
	}
	
	private void refreshFrame() {
		revalidate();
		repaint();
	}
	
	public void customerLogin() {
		getContentPane().removeAll();
		JPanel northPanel = new JPanel(new BorderLayout());
		JLabel northLabel = new JLabel("JHardware", SwingConstants.CENTER);
		
		northPanel.setBackground(Color.BLACK);
		northLabel.setForeground(Color.WHITE);
		northLabel.setFont(new Font("Veranda", Font.BOLD, 30));
		northPanel.add(northLabel);
		this.add(northPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(null); 
		JLabel emailLabel = new JLabel("Email: ");
		JLabel passwordLabel = new JLabel("Password: ");
		emailField = new JTextField(30);
		passwordField = new JPasswordField(30);
		
		// Define consistent dimensions and positions for the form elements
		final int LABEL_X = 50;
		final int FIELD_X = 150; 
		final int LOGIN_BUTTON_X = 150;
		
		final int ELEMENT_WIDTH = 200; 
		final int ELEMENT_HEIGHT = 25; 
		
		final int EMAIL_Y = 30;
		final int PASSWORD_Y = EMAIL_Y + ELEMENT_HEIGHT + 30; 
		final int LOGIN_BUTTON_Y = PASSWORD_Y + ELEMENT_HEIGHT + 30;
		
		
		centerPanel.setBackground(Color.GRAY);
		emailLabel.setForeground(Color.WHITE);
		passwordLabel.setForeground(Color.WHITE);
		
		emailLabel.setBounds(LABEL_X, EMAIL_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		emailField.setBounds(FIELD_X, EMAIL_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		passwordLabel.setBounds(LABEL_X, PASSWORD_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		passwordField.setBounds(FIELD_X, PASSWORD_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		loginButton = new JButton("Login");
		loginButton.setBackground(Color.decode("#ADD1DB"));
		loginButton.setForeground(Color.BLACK);
		loginButton.setBounds(LOGIN_BUTTON_X, LOGIN_BUTTON_Y, ELEMENT_WIDTH - 100, ELEMENT_HEIGHT);
		
		centerPanel.add(emailLabel);
		centerPanel.add(passwordLabel);
		centerPanel.add(loginButton);
		centerPanel.add(emailField);
		centerPanel.add(passwordField);
		this.add(centerPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.setBackground(Color.DARK_GRAY);
		adminButton = new JButton("Admin");
		adminButton.setBackground(Color.decode("#ADD1DB"));
		adminButton.setForeground(Color.BLACK);
		southPanel.add(adminButton);
		this.add(southPanel, BorderLayout.SOUTH);
		
		refreshFrame();
		
	}
	
	public void adminLogin() {
		getContentPane().removeAll();
		JPanel northPanel = new JPanel(new BorderLayout());
		JLabel northLabel = new JLabel("JHardware (admin)", SwingConstants.CENTER);
		
		northPanel.setBackground(Color.BLACK);
		northLabel.setForeground(Color.WHITE);
		northLabel.setFont(new Font("Veranda", Font.BOLD, 30));
		northPanel.add(northLabel);
		this.add(northPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(null); 
		JLabel emailLabel = new JLabel("Email: ");
		JLabel passwordLabel = new JLabel("Password: ");
		emailField = new JTextField(30);
		passwordField = new JPasswordField(30);
		
		// Define consistent dimensions and positions for the form elements
		final int LABEL_X = 50;
		final int FIELD_X = 150; 
		final int LOGIN_BUTTON_X = 150;
		
		final int ELEMENT_WIDTH = 200; 
		final int ELEMENT_HEIGHT = 25; 
		
		final int EMAIL_Y = 30;
		final int PASSWORD_Y = EMAIL_Y + ELEMENT_HEIGHT + 30; 
		final int LOGIN_BUTTON_Y = PASSWORD_Y + ELEMENT_HEIGHT + 30;
		
		
		centerPanel.setBackground(Color.GRAY);
		emailLabel.setForeground(Color.WHITE);
		passwordLabel.setForeground(Color.WHITE);
		
		emailLabel.setBounds(LABEL_X, EMAIL_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		emailField.setBounds(FIELD_X, EMAIL_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		passwordLabel.setBounds(LABEL_X, PASSWORD_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		passwordField.setBounds(FIELD_X, PASSWORD_Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		loginButton = new JButton("Login");
		loginButton.setBackground(Color.decode("#ADD1DB"));
		loginButton.setForeground(Color.BLACK);
		loginButton.setBounds(LOGIN_BUTTON_X, LOGIN_BUTTON_Y, ELEMENT_WIDTH - 100, ELEMENT_HEIGHT);
		
		centerPanel.add(emailLabel);
		centerPanel.add(passwordLabel);
		centerPanel.add(loginButton);
		centerPanel.add(emailField);
		centerPanel.add(passwordField);
		this.add(centerPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.setBackground(Color.DARK_GRAY);
		backButton = new JButton("Back");
		backButton.setBackground(Color.decode("#ADD1DB"));
		backButton.setForeground(Color.BLACK);
		southPanel.add(backButton);
		this.add(southPanel, BorderLayout.SOUTH);
		
		refreshFrame();
		
	}
	
	public String getEmail() {
		return emailField.getText();
	}
	
	public String getPassword(){
        char[] password = passwordField.getPassword();
        String stringPassword = new String(password);
        return stringPassword;
    }
	
	public void setAdminAction(ActionListener listener) {
		adminButton.addActionListener(listener);
	}
	
	public void setLoginAction(ActionListener listener) {
		loginButton.addActionListener(listener);
	}
	
	public void setBackAction(ActionListener listener) {
		backButton.addActionListener(listener);
	}
}
