import java.awt.*;
import javax.swing.JOptionPane;

public class LoginController {
	private AdminModel adminModel;
	private AdminView adminView;
	
	private CustomerModel customerModel;
	private CustomerView customerView;
	
	private LoginView loginView;
	private LoginModel loginModel;
	
	public LoginController(LoginView view, LoginModel model) {
		this.loginView = view;
		this.loginModel = model;
		initCustomerActions();
		
	}
	
	private void initCustomerActions() {
		
		loginView.setAdminAction(e -> {
			loginView.adminLogin();
			initAdminActions();
		});
		
		loginView.setLoginAction(e -> handleCustomerLogin());
	}
	
	private void initAdminActions() {
		
		loginView.setLoginAction(e -> handleAdminLogin());
		
		loginView.setBackAction(e -> {
			loginView.customerLogin();
			initCustomerActions(); 
		});
	}
	
	private void handleAdminLogin() {
		String email = "group4";
		String password = "group4";
		
		String enteredEmail = loginView.getEmail().trim();
		String enteredPassword = loginView.getPassword();
		
		if(enteredEmail.equals(email) && enteredPassword.equals(password)) {
			loginView.dispose();
			AdminView adminView = new AdminView();
			AdminModel adminModel = new AdminModel();
			new AdminController(adminView, adminModel);
			
		}
		else {
			JOptionPane.showMessageDialog(loginView, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void handleCustomerLogin() {
		String enteredEmail = loginView.getEmail().trim();
		String enteredPassword = loginView.getPassword();
		
		if(loginModel.checkCustomer(enteredEmail, enteredPassword)) {
			loginView.dispose();
			CustomerView customerView = new CustomerView();
			CustomerModel customerModel = new CustomerModel();
			new CustomerController(customerView, customerModel);
		}
		else {
			JOptionPane.showMessageDialog(loginView, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}	
