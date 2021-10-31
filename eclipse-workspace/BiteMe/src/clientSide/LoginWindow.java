package clientSide;

import util.Logger;
import util.Logger.Level;

import org.json.simple.JSONObject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class LoginWindow {

	private Stage primaryStage;
	private Scene scene;
	private VBox loginVBox;
	private LoginPortalView view;

	private final int MAX_INPUT = 30;

	@FXML
	private Label loginStatusLabel;
	@FXML
	private Button loginButton;
	@FXML
	private TextArea usernameTextArea;
	@FXML
	private PasswordField passwordField;

	public void init(VBox loginVBox, Stage primaryStage, LoginPortalView view) {
		this.loginVBox = loginVBox;
		this.primaryStage = primaryStage;
		this.view = view;
	}

	public void showWindow() {
		// log
		Logger.log(Level.INFO, "LoginWindow: showing window");
		System.out.println("LoginWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(loginVBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "LoginWindow: exception in showWindow");
				System.out.println("LoginWindow: exception in showWindow");
			}

			usernameTextArea.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));
			passwordField.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));

			primaryStage.setScene(scene);
			primaryStage.show();

			view.ready();
		});
	}

	@FXML
	public void onLoginButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "LoginWindow: onLoginButton CLICK !!!");
		System.out.println("LoginWindow: onLoginButton CLICK !!!");

		JSONObject json = new JSONObject();
		json.put("command", "login");
		json.put("username", usernameTextArea.getText());
		json.put("password", passwordField.getText());

		view.getComController().handleUserAction(json);
	}

	public void onStatusConnected() {
		Platform.runLater(() -> {
			// log
			Logger.log(Level.DEBUG, "LoginWindow: updating status to: connected");
			System.out.println("LoginWindow: updating status to: connected");

			loginStatusLabel.setText("ONLINE");
			loginStatusLabel.setTextFill(Paint.valueOf("GREEN"));
			loginButton.setDisable(false);
		});
	}

	public void onStatusDisconnected() {
		Platform.runLater(() -> {
			// log
			Logger.log(Level.DEBUG, "LoginWindow: updating status to: disconnected");
			System.out.println("LoginWindow: updating status to: disconnected");

			loginStatusLabel.setText("OFFLINE");
			loginStatusLabel.setTextFill(Paint.valueOf("RED"));
			loginButton.setDisable(true);
		});
	}

}
