package clientSide;

import javafx.stage.Stage;
import util.Logger;
import util.Logger.Level;

public class PortalFactory {

	private Stage primaryStage;
	private ComController com;

	public PortalFactory(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	public PortalViewInterface createPortalView(String portalType) {

		if (portalType == null)
			return null;

		switch (portalType) {
		case "login":
			return new LoginPortalView(primaryStage,com);
		case "customer":
			return new CustomerPortalView(primaryStage,com);

		default:
			// log
			Logger.log(Level.WARNING, "PortalFactory: unknown portal type");
			System.out.println("PortalFactory: unknown portal type");
			return null;
		}
	}
}
