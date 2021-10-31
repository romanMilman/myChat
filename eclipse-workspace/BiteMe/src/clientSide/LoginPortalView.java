package clientSide;

import java.io.IOException;

import org.json.simple.JSONObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Logger;
import util.Logger.Level;

public class LoginPortalView implements PortalViewInterface {

	private ComController com;

	// ----- LOGIN variables

	private Stage primaryStage;
	private LoginWindow loginWindow;
	private VBox loginVBox;

	// ----- CONSTRUCTOR

	public LoginPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	@Override
	public void init() {

		try {
			// LOGIN
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/LoginTemplate.fxml"));
			loginVBox = loader.load();
			loginWindow = loader.getController();
			loginWindow.init(loginVBox, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "LoginPortalView: loginWindow initialized");
			System.out.println("LoginPortalView: loginWindow initialized");

			// displays login window
			loginWindow.showWindow();

		} catch (IOException e) {
			Logger.log(Level.WARNING, "LoginPortalView: init: IOException");
			System.out.println("LoginPortalView: init: IOException");
		}
	}

	@Override
	public void ShowScreen(JSONObject descriptor) {

		switch ((String) descriptor.get("command")) {
		case "login":
			loginWindow.onStatusConnected();
			break;

		default:
			break;
		}
	}

	// ----- Functions for loginWindow

	public void ready() {
		JSONObject json = new JSONObject();
		json.put("command", "LoginWindowIsReady");

		com.handleUserAction(json);
	}

	@Override
	public ComController getComController() {
		return com;
	}
}
