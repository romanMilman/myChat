package controllers;

import ocsf.server.ConnectionToClient;
import serverSide.DataBase;
import common.Logger;
import common.Logger.Level;

public class PortalViewControllerFactory {

	private DataBase db;
	private ComController com;

	public PortalViewControllerFactory(DataBase db, ComController com) {
		this.db = db;
		this.com = com;
	}

	public PortalViewController createPortalViewController(String portalType, ConnectionToClient connection) {
		if (portalType == null)
			return null;

		switch (portalType) {
		case "login":
			return new LoginPortalViewController(db, com, connection);
		case "branch manager":
			return new BranchManagerPortalViewController(db, com, connection);

		default:
			// log
			Logger.log(Level.WARNING, "PortalFactory: unknown portal type");
			System.out.println("PortalFactory: unknown portal type");
			return null;
		}
	}
}
