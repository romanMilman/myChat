package serverSide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;
import ocsf.server.*;

public class CommunicationManager extends AbstractServer {

	private DataBase db;

	final private String HASHMAP_KEY = "client";

	// ---------------------------------CONSTRUCTOR
	public CommunicationManager(int port, DataBase db) {
		super(port);
		this.db = db;
	}

	public void start() {
		try {
			listen();
		} catch (IOException e) {
			Logger.log(Level.WARNING, "MANAGER: listen exception in start");
			System.out.println("MANAGER: listen exception in start");
		}
		pingClients();
	}

	// TODO probably not thread safe
	@Override
	protected void clientConnected(ConnectionToClient connection) {
		Client c = new Client(connection, db, this);
		connection.setInfo(HASHMAP_KEY, c);

		// log
		Logger.log(Level.DEBUG,
				"MANAGER: " + c.getUserName() + " " + connection.getInetAddress().toString() + ": has connected");
		System.out.println(
				"MANAGER: " + c.getUserName() + " " + connection.getInetAddress().toString() + ": has connected");
	}

	@Override
	synchronized protected void clientDisconnected(ConnectionToClient connection) {
	}

	@Override
	synchronized protected void clientException(ConnectionToClient connection, Throwable exception) {
		Client c = (Client) connection.getInfo(HASHMAP_KEY);
		c.onDisconnect();

		// log
		Logger.log(Level.DEBUG, "MANAGER: " + c.getUserName() + " " + connection.toString() + ": has disconnected");
		System.out.println("MANAGER: " + c.getUserName() + " " + connection.toString() + ": has disconnected");

		c.sendRegistrationNotification();
	}

	@Override
	protected void listeningException(Throwable exception) {
		// log
		Logger.log(Level.DEBUG, "MANAGER: listening exception");
		System.out.println("MANAGER: listening exception");

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			public void run() {
				// log
				Logger.log(Level.DEBUG, "MANAGER: start listening again...");
				System.out.println("MANAGER: start listening again...");
				try {
					listen();
				} catch (IOException e) {
					Logger.log(Level.WARNING, "MANAGER: IOException in listeningException");
					System.out.println("MANAGER: IOException in listeningException");
				}
			};
		};
		timer.schedule(task, 10000);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient connection) {
		Client client = (Client) connection.getInfo(HASHMAP_KEY);
		client.onHandleMsg(msg);
	}

	/*
	 * Loops through all clients, return the client that matches the 'UserName'. if
	 * client not found return null.
	 */
	public Client findClient(String username) {
		for (Thread t : getClientConnections()) {
			ConnectionToClient c = ((ConnectionToClient) t);
			Client client = (Client) c.getInfo(HASHMAP_KEY);

			if (client.getUserName().equals(username) && client.isAuthorized())
				return client;
		}
		return null;
	}

	public ArrayList<Client> getAllClients(String username) {
		ArrayList<Client> arr = new ArrayList<>();
		Thread[] threadArr = getClientConnections();

		if (threadArr.length == 1)
			return null;

		for (Thread t : threadArr) {
			ConnectionToClient c = ((ConnectionToClient) t);
			Client client = (Client) c.getInfo(HASHMAP_KEY);

			if (!(username.equals(client.getUserName())))
				arr.add(client);
		}

		return arr;
	}

	public ArrayList<String> getAllClientsUserNames(ArrayList<Client> arr) {
		ArrayList<String> usernames = new ArrayList<>();

		if (arr == null)
			return usernames;

		for (Client c : arr) {
			usernames.add(c.getUserName());
		}
		return usernames;
	}

	public void pingClients() {
		Timer timer;
		timer = new Timer();
		TimerTask task = new TimerTask() {

			public void run() {
				sendToAllClients("{\"MessageType\":\"PING\"}" + '\n');
			}

		};
		timer.schedule(task, 60000, 60000);
	}

}
