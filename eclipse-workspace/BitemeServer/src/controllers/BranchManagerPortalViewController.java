package controllers;

import java.io.IOException;

import org.json.simple.JSONObject;

import ocsf.server.ConnectionToClient;
import serverSide.DataBase;
import common.Logger;
import common.Message;
import common.Parser;
import common.Logger.Level;

public class BranchManagerPortalViewController implements PortalViewController {

	private DataBase db;
	private ComController com;
	private ConnectionToClient connection;

	public BranchManagerPortalViewController(DataBase db, ComController com, ConnectionToClient connection) {
		this.db = db;
		this.com = com;
		this.connection = connection;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleCommandFromClient(JSONObject json) {
		JSONObject response = new JSONObject();

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalViewController: handleCommandFromClient: " + json);
		System.out.println("BranchManagerPortalViewController: handleCommandFromClient: " + json);

		switch (Message.getValue(json, "command")) {
		case "logout was pressed":
			handleLogout(json);
			break;
		case "client register was pressed":
			response.put("command", "update");
			response.put("update", "show registration window");
			try {
				connection.sendToClient(Parser.encode(response));
			} catch (IOException e) {
				Logger.log(Level.WARNING,
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
				System.out.println(
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
			}
			break;
		case "regular registration button was pressed":
			response.put("command", "update");
			response.put("update", "show regular registration window");
			try {
				connection.sendToClient(Parser.encode(response));
			} catch (IOException e) {
				Logger.log(Level.WARNING,
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
				System.out.println(
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
			}
			break;
		case "bussiness registration button was pressed":
			response.put("command", "update");
			response.put("update", "show bussiness registration window");
			try {
				connection.sendToClient(Parser.encode(response));
			} catch (IOException e) {
				Logger.log(Level.WARNING,
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
				System.out.println(
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
			}
			break;
		case "registration save button was pressed":
			handleRegistration(json);
			break;

		default:

			Logger.log(Level.DEBUG, "BranchManagerPortalViewController: unknown message in handleCommandFromClient");
			System.out.println("BranchManagerPortalViewController: unknown message in handleCommandFromClient");
			break;
		}

	}

	@SuppressWarnings("unchecked")
	private void handleRegistration(JSONObject msg) {
		JSONObject response = new JSONObject();

		switch (Message.getValue(msg, "registration")) {
		case "regular":
			response = db.registerRegularClient(msg);
			try {
				connection.sendToClient(Parser.encode(response));
			} catch (IOException e) {
				Logger.log(Level.WARNING,
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
				System.out.println(
						"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
			}
			break;
		case "business":
			int id = -1;
			if ((id = db.isEmployerExists(msg)) == -1) {
				response.put("command", "update");
				response.put("update", "could not add business user to database");
			} else {
				response = db.registerBusinessClient(msg, id);
			}
			try {
				connection.sendToClient(Parser.encode(response));
			} catch (IOException e) {
				Logger.log(Level.WARNING,
						"BranchManagerPortalViewController: IOException was caught in handleRegistration");
				System.out.println(
						"BranchManagerPortalViewController: IOException was caught in handleRegistration");
			}
			break;

		default:
			Logger.log(Level.WARNING, "BranchManagerPortalViewController: unknown message in handleRegistration");
			System.out.println("BranchManagerPortalViewController: unknown message in handleRegistration");
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private void handleLogout(JSONObject msg) {
		JSONObject response = new JSONObject();

		response.put("command", "handshake");
		response.put("portalType", "login");
		response.put("status", "ok");
		try {
			// log
			Logger.log(Level.DEBUG,
					"BranchManagerPortalViewController: Client = " + connection.toString() + " logged out");
			System.out.println("BranchManagerPortalViewController: Client = " + connection.toString() + " logged out");

			connection.sendToClient(Parser.encode(response));
			com.switchPortal(connection, "login");

		} catch (IOException e) {
			Logger.log(Level.WARNING,
					"BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
			System.out.println("BranchManagerPortalViewController: IOException was caught in handleCommandFromClient");
		}
	}

}
