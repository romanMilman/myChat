package serverSide;

import java.sql.SQLException;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;

public class Server {

	// DataBase
	private static DataBase db;

	// Manager
	private static CommunicationManager mngr;

	// SERVERS PORT
	final private static int MY_PORT = 45111;

	public static void main(String[] args) {
		db = new DataBase();
		mngr = new CommunicationManager(MY_PORT, db);

		try {
			mngr.start(); // Start listening for connections
			db.initialize();

		} catch (SQLException ex) {/* handle any SQL errors */
			Logger.log(Level.WARNING, "SQLException: " + ex.getMessage());
			Logger.log(Level.WARNING, "SQLState: " + ex.getSQLState());
			Logger.log(Level.WARNING, "VendorError: " + ex.getErrorCode());

			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (Exception ex) {
			Logger.log(Level.WARNING, "ERROR - Could not listen for clients!");
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
}
