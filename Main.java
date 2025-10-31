public class Main {
	public static void main(String[] args) {
		LoginView view = new LoginView();
		LoginModel model = new LoginModel();
		new LoginController(view, model);
	}
}
