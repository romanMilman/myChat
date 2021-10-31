package clientSide;

import org.json.simple.JSONObject;

import javafx.application.Application;
import javafx.stage.Stage;
import util.Logger;
import util.Logger.Level;

public class BMClient extends Application {

	private final static String DEFAULT_IP = "localhost";
	private final static int DEFAULT_PORT = 5555;
	private PortalFactory factory;

	public static void main(String[] args) {

		Logger.init();
		Logger.setLevel(Level.DEBUG);

		// log
		System.out.println("BMClient: Logger initialized");

		// Launch() is blocking
		launch();

		// log
		Logger.log(Level.DEBUG, "BMClient: javaFX window application stoped");
		System.out.println("BMClient: javaFX window application stoped");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		ComController com = new ComController(DEFAULT_IP, DEFAULT_PORT);
		factory = new PortalFactory(primaryStage, com);
		com.setPortalFactory(factory);

		// log
		Logger.log(Level.DEBUG, "BMClient: ComController initialized");
		System.out.println("BMClient: ComController initialized");

		com.start();
	}

}
