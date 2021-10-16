package clientSide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginWindow {

	private final int MAX_INPUT = 30;

	private UIManager uiMngr;
	private VBox loginVBox;
	private Stage primaryStage;
	private Scene scene;

	@FXML
	private Label loginStatusLabel;
	@FXML
	private Button loginButton;
	@FXML
	private Button registerButton;
	@FXML
	private TextArea loginUsernameText;
	@FXML
	private PasswordField loginPasswordText;

	public void init(UIManager uiMngr, VBox loginVBox, Stage primaryStage) {
		this.loginVBox = loginVBox;
		this.primaryStage = primaryStage;
		this.uiMngr = uiMngr;
	}

	public void showWindow() {
		// log
		Logger.log(Level.DEBUG, "LoginWindow: showing window");
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

			loginUsernameText.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));
			loginPasswordText.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));

			primaryStage.setScene(scene);
			primaryStage.show();
		});
	}

	public void updateStatus(Status status) {
		Platform.runLater(() -> {
			// log
			Logger.log(Level.DEBUG, "LoginWindow: updating status to: " + status);
			System.out.println("LoginWindow: updating status to: " + status);

			if (status == Status.CONNECTED) {
				loginStatusLabel.setText("ONLINE");
				loginStatusLabel.setTextFill(Paint.valueOf("GREEN"));
				loginButton.setDisable(false);
			} else {
				loginStatusLabel.setText("OFFLINE");
				loginStatusLabel.setTextFill(Paint.valueOf("RED"));
				loginButton.setDisable(true);
			}
		});
	}

	@FXML
	public void onLoginButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG,
				"LoginWindow: Username: " + loginUsernameText.getText() + ", Password: " + loginPasswordText.getText());
		System.out.println(
				"LoginWindow: Username: " + loginUsernameText.getText() + ", Password: " + loginPasswordText.getText());

		if ((!validInput(loginUsernameText.getText())) || (!validInput(loginPasswordText.getText()))) {
			// log
			Logger.log(Level.DEBUG, "LoginWindow: illegal username or password");
			System.out.println("LoginWindow: illegal username or password");

			showError();

			return;
		}

		uiMngr.getController().login(loginUsernameText.getText(), loginPasswordText.getText());
	}

	@FXML
	public void onRegisterButton(ActionEvent event) {
		uiMngr.getController().setWindow(Window.REGISTER_WINDOW);
		uiMngr.getController().openWindow();
	}

	public Boolean validInput(String input) {
		Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(input);

		if (m.find() || (input.length() > 18) || (input.length() < 2))
			return false;

		return true;
	}

	public void showError() {
		// log
		Logger.log(Level.DEBUG, "LoginWindow: showing error");
		System.out.println("LoginWindow: showing error");

		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("ERROR");
			window.setMinWidth(50);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText("ERROR IN LOGIN: Username or Password incorrect");

			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		});
	}

	public void fatalError() {
		// log
		Logger.log(Level.WARNING, "LoginWindow: showing error");
		System.out.println("LoginWindow: showing error");

		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("ERROR");
			window.setMinWidth(50);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText("Can't reach server, please try again later...");

			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		});
	}
}
