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

public class RegisterWindow {

	private final int MAX_INPUT = 30;

	private UIManager uiMngr;
	private VBox registerVBox;
	private Stage primaryStage;
	private Scene scene;

	@FXML
	private Label registerStatusLabel;
	@FXML
	private Button registerButton;
	@FXML
	private Button backButton;
	@FXML
	private TextArea registerUsernameText;
	@FXML
	private PasswordField registerPasswordText;

	public void init(UIManager uiMngr, VBox registerVBox, Stage primaryStage) {
		this.registerVBox = registerVBox;
		this.primaryStage = primaryStage;
		this.uiMngr = uiMngr;
	}

	public void showWindow() {
		// log
		Logger.log(Level.DEBUG, "RegisterWindow: showing window");
		System.out.println("RegisterWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(registerVBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "RegisterWindow: exception in showWindow");
				System.out.println("RegisterWindow: exception in showWindow");
			}

			registerUsernameText.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));
			registerPasswordText.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));

			primaryStage.setScene(scene);
			primaryStage.show();
		});
	}

	public void updateStatus(Status status) {
		Platform.runLater(() -> {
			if (status == Status.CONNECTED) {
				registerStatusLabel.setText("ONLINE");
				registerStatusLabel.setTextFill(Paint.valueOf("GREEN"));
				registerButton.setDisable(false);
			} else {
				registerStatusLabel.setText("OFFLINE");
				registerStatusLabel.setTextFill(Paint.valueOf("RED"));
				registerButton.setDisable(true);
			}
		});
	}

	// TODO make controller switch windows
	@FXML
	public void onBackButton(ActionEvent event) {
		uiMngr.getController().setWindow(Window.LOGIN_WINDOW);
		uiMngr.getController().openWindow();
	}

	@FXML
	public void onRegisterButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG, "RegisterWindow: Username: " + registerUsernameText.getText() + ", Password: "
				+ registerPasswordText.getText());
		System.out.println("RegisterWindow: Username: " + registerUsernameText.getText() + ", Password: "
				+ registerPasswordText.getText());

		if ((!validInput(registerUsernameText.getText())) || (!validInput(registerPasswordText.getText()))) {
			// log
			Logger.log(Level.DEBUG, "RegisterWindow: illegal username or password");
			System.out.println("RegisterWindow: illegal username or password");
			showError();

			return;
		}

		uiMngr.getController().register(registerUsernameText.getText(), registerPasswordText.getText());
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
		Logger.log(Level.DEBUG, "RegisterWindow: showing error");
		System.out.println("RegisterWindow: showing error");

		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("ERROR");
			window.setMinWidth(50);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText("ERROR IN REGISTRATION: Username or Password invalid");

			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		});
	}
}
