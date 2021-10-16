package clientSide;

import java.util.ArrayList;
import java.util.HashMap;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UIManager {

	// VARIABLES
	private ControllerUiAPI controller;

	private Stage primaryStage;

	private ChatWindow chatWindow;
	private LoginWindow loginWindow;
	private RegisterWindow registerWindow;

	// CONSTUCTOR
	public UIManager(Controller controller) {
		this.controller = controller;
	}

	// WINDOWS
	public void setChatWindow(ChatWindow chatWindow, HBox chatHBox, Stage primaryStage) {
		this.chatWindow = chatWindow;
		chatWindow.init(this, chatHBox, primaryStage);

		// log
		Logger.log(Level.DEBUG, "UIMngr: chat window has been initialized");
		System.out.println("UIMngr: chat window has been initialized");
	}

	public void setLoginWindow(LoginWindow loginWindow, VBox loginVBox, Stage primaryStage) {
		this.loginWindow = loginWindow;
		loginWindow.init(this, loginVBox, primaryStage);

		// log
		Logger.log(Level.DEBUG, "UIMngr: login window has been initialized");
		System.out.println("UIMngr: login window has been initialized");
	}

	public void setRegisterWindow(RegisterWindow registerWindow, VBox registerVBox, Stage primaryStage) {
		this.registerWindow = registerWindow;
		registerWindow.init(this, registerVBox, primaryStage);

		// log
		Logger.log(Level.DEBUG, "UIMngr: register window has been initialized");
		System.out.println("UIMngr: register window has been initialized");
	}

	// STAGE
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	// ################################# FUNC FOR CONTROLLER ##################
	public void openLoginWindow() {
		loginWindow.showWindow();
	}

	public void openChatWindow() {
		chatWindow.showWindow();
	}

	public void openRegisterWindow() {
		registerWindow.showWindow();
	}

	public void updateStatus(Status status) {
		// log
		Logger.log(Level.DEBUG, "UIMngr: recived status: " + status);
		System.out.println("UIMngr: recived status: " + status);

		if (controller.getWindow() == Window.LOGIN_WINDOW)
			loginWindow.updateStatus(status);
		if (controller.getWindow() == Window.REGISTER_WINDOW)
			registerWindow.updateStatus(status);
	}

	public void updateFriendsList(ArrayList<User> list) {
		chatWindow.buildFriendsList(list);
	}

	public void buildChatList(ArrayList<ChatMessage> list) {
		chatWindow.buildChatList(list);
	}

	public void updateChat(String sourceUserName) {
		// log
		Logger.log(Level.DEBUG, "UIMngr: updating chat with: " + sourceUserName);
		System.out.println("UIMngr: updating chat with: " + sourceUserName);

		chatWindow.updateChat(sourceUserName);
	}

	public void fatalError() {
		loginWindow.fatalError();
	}

	// ################################# FUNC FOR WINDOWS ####################
	public ControllerUiAPI getController() {
		return controller;
	}

	public void showError() {
		Window currWindow = controller.getWindow();
		if (currWindow == Window.REGISTER_WINDOW) {
			registerWindow.showError();
		} else if (currWindow == Window.LOGIN_WINDOW) {
			loginWindow.showError();
		}
	}
}
