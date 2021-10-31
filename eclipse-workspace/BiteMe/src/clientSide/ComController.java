package clientSide;

import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

import util.*;
import util.Logger.Level;

public class ComController {

	private PortalViewInterface view;

	private String ip;
	private int port;
	private PortalFactory factory;

	public ComController(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void setPortalFactory(PortalFactory factory) {
		this.factory = factory;
	}

	public void start() {
		view = factory.createPortalView("login");
		view.init();

		// log
		Logger.log(Level.DEBUG, "ComController: PortalView initialized");
		System.out.println("ComController: PortalView initialized");

		// TODO openConnection() will be used when ComController will implement
		// AbstractClient
		// openConnection(); // blocking?
	}

	public void connectionEstablished() {
		// log
		Logger.log(Level.DEBUG, "ComController: connection established");
		System.out.println("ComController: connection established");

		JSONObject json = new JSONObject();
		json.put("command", "login");

		view.ShowScreen(json);
	}

	public void handleMessageFromServer(Object msg) {

		if (msg == null) {
			// log
			Logger.log(Level.WARNING, "ComController: Received null from server");
			System.out.println("ComController: Received null from server");
			return;
		}

		// log
		Logger.log(Level.DEBUG, "ComController: Received message from server");
		System.out.println("ComController: Received message from server");

		// JSONObject json = Parser.decode(msg);

		// temporary solution for simulation
		JSONObject json = (JSONObject) msg;

		// TODO create Message class to handle all the converts
		switch ((String) json.get("command")) {
		case "handshake":
			// log
			Logger.log(Level.INFO, "ComController: messageType: handshake");
			System.out.println("ComController: messageType: handshake");

			view = factory.createPortalView((String) json.get("portalType"));
			view.init();
			break;

		default:
			// log
			Logger.log(Level.INFO, "ComController: messageType: undefined");
			System.out.println("ComController: messageType: undefined");
			break;
		}
	}

	public void handleUserAction(JSONObject msg) {
		Timer timer;

		if (msg.get("command") == "LoginWindowIsReady") {
			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					connectionEstablished();
				};
			};

			timer.schedule(task, 2000);

		} else if (msg.get("command") == "login") {
			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					JSONObject json = new JSONObject();
					json.put("command", "handshake");
					json.put("status", "ok");
					json.put("portalType", "customer");

					handleMessageFromServer(json);
				};
			};

			timer.schedule(task, 2000);

		}

	}
}
