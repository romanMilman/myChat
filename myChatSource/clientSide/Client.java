package clientSide;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Client extends Application {

	private static UIManager uiMngr;
	private static CommunicationManager comMngr;
	private static Controller controller;

	final public static int DEFAULT_PORT = 5555;

	public static void main(String[] args) {

		System.out.println("Client: Logger initialized");
		Logger.init();
		Logger.setLevel(Level.DEBUG);

		if (args.length != 2) {
			Logger.log(Level.DEBUG, "Client: invalid params");
			System.out.println("Client: invalid params");
			return;
		}

		Logger.log(Level.DEBUG, "Client: ip entered = " + args[0]);
		System.out.println("Client: ip entered = " + args[0]);

		controller = new Controller();
		uiMngr = new UIManager(controller);

		String adrString = "";

		try {
			InetAddress address = InetAddress.getByName(args[0]);
			adrString = address.getHostAddress().toString();

			// log
			Logger.log(Level.DEBUG, "Client: connecting to : " + adrString);
			System.out.println("Client: connecting to : " + adrString);

		} catch (UnknownHostException e) {
			Logger.log(Level.WARNING, "Client: failed to resolve : " + args[0] + "\nTERMINATING PROG");
			System.out.println("Client: failed to resolve : " + args[0] + "\nTERMINATING PROG");
			System.exit(-1);
		}

		comMngr = new CommunicationManager(adrString, Integer.parseInt(args[1]), controller);

		controller.init(uiMngr, comMngr);

		launch();

		controller.stop();

		Logger.stop();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		HBox chatHBox;
		VBox loginVBox, registerVBox;
		ChatWindow chatWindow;
		LoginWindow loginWindow;
		RegisterWindow registerWindow;

		Logger.log(Level.DEBUG, "Client: loading fxml");
		System.out.println("Client: loading fxml");
		try {
			// CHAT
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ChatTemplate.fxml"));
			chatHBox = loader.load();
			chatWindow = loader.getController();
			uiMngr.setChatWindow(chatWindow, chatHBox, primaryStage);
			uiMngr.setStage(primaryStage);

			// LOGIN
			FXMLLoader loader2 = new FXMLLoader();
			loader2.setLocation(getClass().getResource("LoginTemplate.fxml"));
			loginVBox = loader2.load();
			loginWindow = loader2.getController();
			uiMngr.setLoginWindow(loginWindow, loginVBox, primaryStage);

			// REGISTER
			FXMLLoader loader3 = new FXMLLoader();
			loader3.setLocation(getClass().getResource("RegisterTemplate.fxml"));
			registerVBox = loader3.load();
			registerWindow = loader3.getController();
			uiMngr.setRegisterWindow(registerWindow, registerVBox, primaryStage);

			// COMM
			controller.run();

		} catch (IOException e) {
			Logger.log(Level.WARNING, "Client: IOException");
			System.out.println("Client: IOException");
			return;
		}
	}
}
