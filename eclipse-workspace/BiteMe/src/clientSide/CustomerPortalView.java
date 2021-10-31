package clientSide;

import java.io.IOException;
import org.json.simple.JSONObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Logger;
import util.Logger.Level;

public class CustomerPortalView implements PortalViewInterface {

	private ComController com;

	// ----- LOGIN variables

	private Stage primaryStage;
	private CustomerWindow customerWindow;
	private VBox homePageVBox;

	public CustomerPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	@Override
	public void init() {

		try {
			// LOGIN
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/CustomerHomepageTemplate.fxml"));
			homePageVBox = loader.load();
			customerWindow = loader.getController();
			customerWindow.init(homePageVBox, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "CustomerPortalView: loginWindow initialized");
			System.out.println("CustomerPortalView: loginWindow initialized");

			// displays login window
			customerWindow.showWindow();

		} catch (IOException e) {
			Logger.log(Level.WARNING, "CustomerPortalView: init: IOException");
			System.out.println("CustomerPortalView: init: IOException");
		}
	}

	@Override
	public void ShowScreen(JSONObject descriptor) {

		switch ((String) descriptor.get("command")) {
		case "RestaurantList":
			showRestaurantsList(descriptor);
			break;

		default:
			break;
		}
	}

	private void showRestaurantsList(JSONObject descriptor) {

	}

	@Override
	public void ready() {
		JSONObject json = new JSONObject();
		json.put("command", "CustomerHomepageWindowIsReady");
		com.handleUserAction(json);
	}

	@Override
	public ComController getComController() {
		return com;
	}
}
