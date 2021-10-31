package clientSide;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Logger;
import util.Logger.Level;

public class CustomerWindow {

	private Stage primaryStage;
	private Scene scene;
	private VBox homePageVBox;
	private CustomerPortalView view;

	@FXML
	private Label welcomeLabel;
	@FXML
	private Button orderButton;
	@FXML
	private Button receiveOrderButton;
	@FXML
	private Button backButton;

	public void init(VBox homePageVBox, Stage primaryStage, CustomerPortalView view) {
		this.homePageVBox = homePageVBox;
		this.primaryStage = primaryStage;
		this.view = view;

	}

	public void showWindow() {
		// log
		Logger.log(Level.INFO, "CustomerWindow: showing window");
		System.out.println("CustomerWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(homePageVBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "LoginWindow: exception in showWindow");
				System.out.println("LoginWindow: exception in showWindow");
			}

			primaryStage.setScene(scene);
			primaryStage.show();

			view.ready();
		});
	}
}
