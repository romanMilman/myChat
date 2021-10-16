package serverSide;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import communicationUtil.Logger;
import communicationUtil.Parser;
import communicationUtil.Logger.Level;
import ocsf.server.*;

public class Client {

	private final int MAX_STREAM_SIZE = 10000;

	private ConnectionToClient connection;
	private String userName;
	private DataBase db;
	private CommunicationManager mngr;
	private Boolean isAuthorized;
	private Message message;
	private String communicationBuffer = "";

	public Client(ConnectionToClient connection, DataBase db, CommunicationManager mngr) {
		this.connection = connection;
		this.db = db;
		this.mngr = mngr;
		this.isAuthorized = false;
		message = new Message();
	}

	public void setIsAuthorized(Boolean yesNo) {
		isAuthorized = yesNo;
	}

	public Boolean isAuthorized() {
		return isAuthorized;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getUserName() {
		return userName;
	}

	public ConnectionToClient getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		return connection.toString();
	}

	public void onHandleMsg(Object msg) {
		String stream = (String) msg;
		for (int i = 0; i < stream.length(); i++) {
			if (stream.charAt(i) == '\n') {
				communicationBuffer += '\n';
				processMsg(communicationBuffer);
				communicationBuffer = "";
			} else {
				communicationBuffer += stream.charAt(i);
				if (communicationBuffer.length() >= MAX_STREAM_SIZE) {
					communicationBuffer = "";
					// log
					Logger.log(Level.DEBUG, "Client: recived too long stream, clearning!");
					System.out.println("Client: recived too long stream, clearning!");
				}
			}
		}
	}

	public void processMsg(Object msg) {

		if (!message.parse(msg)) {
			Logger.log(Level.DEBUG, "CLIENT: " + getUserName() + ": JSON failed: " + msg);
			System.out.println("CLIENT: " + getUserName() + ": JSON failed: " + msg);
			return;
		}
		// log
		Logger.log(Level.DEBUG, "CLIENT: Message recived: " + (String) msg);
		System.out.println("CLIENT: Message recived: " + (String) msg);

		// NOT AUTHORIZED
		if (!isAuthorized()) {

			// log
			Logger.log(Level.DEBUG, "CLIENT: not authorized client requested: " + message.getType());
			System.out.println("CLIENT: not authorized client requested: " + message.getType());

			switch (message.getType()) {
			case "AUTORIZATION_REQUEST":
				handleAuthorizationRequest(message);
				break;
			case "REGISTRATION_REQUEST":
				handleRegistrationRequest(message);
				break;
			case "PING":
				Logger.log(Level.DEBUG, "CLIENT: recived ping from: " + getUserName());
				System.out.println("CLIENT: recived ping from: " + getUserName());
				break;
			default:
				Logger.log(Level.DEBUG, "CLIENT: this message is not an option");
				System.out.println("CLIENT: this message is not an option");
				break;
			}

		} else { // IS AUTHORIZED

			// log
			Logger.log(Level.DEBUG,
					"CLIENT: authorized client (" + getUserName() + ") requested: " + message.getType());
			System.out.println("CLIENT: authorized client (" + getUserName() + ") requested: " + message.getType());

			switch (message.getType()) {
			case "TEXT":
				handleText(message);
				break;
			case "ALL_CHAT_REQUEST":
				handleAllChatReq(message);
				break;
			case "NEW_CHAT_REQUEST":
				handleNewChatReq(message);
				break;
			case "FRIEND_LIST_REQUEST":
				handleFriendListReq(message);
				break;
			case "FRIEND_STATUS_REQUEST":
				break;
			case "PING":
				Logger.log(Level.DEBUG, "CLIENT: recived ping from: " + getUserName());
				System.out.println("CLIENT: recived ping from: " + getUserName());
				break;
			default:
				Logger.log(Level.DEBUG, "CLIENT: this message is not an option");
				System.out.println("CLIENT: this message is not an option");
				break;
			}
		}
	}

	// TODO decide what to do, either only 1 can log in the same account OR handle
	// multiple users...
	public void handleAuthorizationRequest(Message msg) {
		String user = msg.getUserName();
		String psw = msg.getPassword();

		if (db.approveUser(user, psw)) {
			setIsAuthorized(true);
			setUserName(user);
			sendAuthorizationResult(true);
			sendRegistrationNotification();
		} else {
			sendAuthorizationResult(false);
		}

		// log
		Logger.log(Level.DEBUG, "CLIENT: " + userName + ": authorization is: " + isAuthorized());
		System.out.println("CLIENT: " + userName + ": authorization is: " + isAuthorized());
	}

	public void handleRegistrationRequest(Message msg) {
		String user = msg.getUserName();
		String psw = msg.getPassword();

		if (db.findUser(user)) {
			sendRegistrationResult(false);
		} else {
			db.addUser(user, psw);
			setIsAuthorized(true);
			setUserName(user);
			sendRegistrationResult(true);
			sendRegistrationNotification();
		}

		// log
		Logger.log(Level.DEBUG, "CLIENT: " + userName + ": registration is: " + isAuthorized());
		System.out.println("CLIENT: " + userName + ": registration is: " + isAuthorized());
	}

	// TODO client might disconnect during SendTextNotification
	// disable the option client can send him self msg's
	public void handleText(Message msg) {

		// return if destination is not a registered user or its himself.
		if (!db.findUser(msg.getDestinationUserName()) || (msg.getDestinationUserName().equals(getUserName())))
			return;

		db.addTextMsg(getUserName(), msg.getDestinationUserName(), msg.getText());

		Client client = mngr.findClient(msg.getDestinationUserName());
		if ((client != null) && client.isAuthorized())
			client.sendTextNotification(getUserName());

		// log
		Logger.log(Level.DEBUG, "CLIENT: " + userName + ": sent text: " + msg.getText());
		System.out.println("CLIENT: " + userName + ": sent text: " + msg.getText());
	}

	// TODO not sure if JSONArray maintains order
	@SuppressWarnings("unchecked")
	public void handleAllChatReq(Message msg) {

		JSONObject jsonObj = new JSONObject();
		JSONArray list = new JSONArray();

		// return if destination is not a registered user
		if (!db.findUser(msg.getDestinationUserName()))
			return;

		ResultSet rs = db.getAllChat(getUserName(), msg.getDestinationUserName());

		jsonObj.put("MessageType", "ALL_CHAT_REQUEST");

		try {

			if (rs == null) {
				String jsonText = Parser.parse(jsonObj);
				connection.sendToClient(jsonText);
				return;
			}

			do {
				JSONObject row = new JSONObject();

				row.put("Text", rs.getString("text"));
				row.put("Date", rs.getString("date"));

				if (getUserName().equals(rs.getString("source")))
					row.put("Me", "true");
				else
					row.put("Me", "false");

				list.add(row);
			} while (rs.next());

			jsonObj.put("Chat", list);
			String jsonText = Parser.parse(jsonObj);
			connection.sendToClient(jsonText);

		} catch (SQLException e) {
			Logger.log(Level.WARNING, "CLIENT: SQLException in handleAllChatReq");
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in handleAllChatReq");
		}
	}

	@SuppressWarnings("unchecked")
	public void handleNewChatReq(Message msg) {

		JSONObject jsonObj = new JSONObject();
		JSONArray list = new JSONArray();

		// return if destination is not a registered user
		if (!db.findUser(msg.getDestinationUserName()))
			return;

		ResultSet rs = db.getNewChat(getUserName(), msg.getDestinationUserName(), msg.getTime(), msg.getDate());

		jsonObj.put("MessageType", "NEW_CHAT_REQUEST");

		try {

			if (rs == null) {
				String jsonText = Parser.parse(jsonObj);
				connection.sendToClient(jsonText);
				return;
			}

			do {
				JSONObject row = new JSONObject();

				row.put("Text", rs.getString("text"));
				row.put("Date", rs.getString("date"));

				if (getUserName().equals(rs.getString("source")))
					row.put("Me", "true");
				else
					row.put("Me", "false");

				list.add(row);
			} while (rs.next());

			jsonObj.put("Chat", list);
			String jsonText = Parser.parse(jsonObj);
			connection.sendToClient(jsonText);

		} catch (SQLException e) {
			Logger.log(Level.WARNING, "CLIENT: SQLException in handleNewChatReq");
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in handleNewChatReq");
		}
	}

	@SuppressWarnings("unchecked")
	public void handleFriendListReq(Message msg) {

		JSONObject jsonObj = new JSONObject();
		JSONArray list = new JSONArray();

		ResultSet rs = db.getFriendList(userName);

		jsonObj.put("MessageType", "FRIEND_LIST_REQUEST");

		try {
			// log
			Logger.log(Level.DEBUG, "Client: handling friend list request");
			System.out.println("Client: handling friend list request");

			if (rs == null) {
				// log
				Logger.log(Level.DEBUG, "Client: sending empty friends list");
				System.out.println("Client: sending empty friends list");

				String jsonText = Parser.parse(jsonObj);
				connection.sendToClient(jsonText);
				return;
			}

			ArrayList<String> onlineUsers = mngr.getAllClientsUserNames(mngr.getAllClients(userName));

			do {
				JSONObject row = new JSONObject();

				row.put("Username", rs.getString("username"));

				if (onlineUsers.contains(rs.getString("username")))
					row.put("Status", "online");
				else
					row.put("Status", "offline");

				list.add(row);
			} while (rs.next());

			// log
			Logger.log(Level.DEBUG, "Client: sending friends list");
			System.out.println("Client: sending friends list");

			jsonObj.put("FriendList", list);
			String jsonText = Parser.parse(jsonObj);

			// log
			Logger.log(Level.DEBUG, "Client: " + jsonText);
			System.out.println("Client: " + jsonText);
			connection.sendToClient(jsonText);

		} catch (SQLException e) {
			Logger.log(Level.WARNING, "CLIENT: SQLException in handleFriendListReq");
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in handleFriendListReq");
		}

	}

	// TODO do something smart
	public void onDisconnect() {
	}

	public void disconnect() {
		try {
			connection.close();
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in disconnect");
		}
	}

	@SuppressWarnings("unchecked")
	private void sendAuthorizationResult(Boolean yesNo) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("MessageType", "AUTORIZATION_REQUEST");
		jsonObj.put("Result", yesNo);
		String jsonText = Parser.parse(jsonObj);

		try {
			connection.sendToClient(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in sendAuthorizationResult");
			disconnect();
		}
	}

	@SuppressWarnings("unchecked")
	private void sendRegistrationResult(Boolean yesNo) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("MessageType", "REGISTRATION_REQUEST");
		jsonObj.put("Result", yesNo);
		String jsonText = Parser.parse(jsonObj);

		try {
			connection.sendToClient(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in sendRegistrationResult");
			disconnect();
		}
	}

	@SuppressWarnings("unchecked")
	private void sendTextNotification(String sourceUserName) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("MessageType", "NOTIFICATION");
		jsonObj.put("Notification_type", "text");
		jsonObj.put("SourceUserName", sourceUserName);
		String jsonText = Parser.parse(jsonObj);

		try {
			connection.sendToClient(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "CLIENT: IOException in sendTextNotification");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendRegistrationNotification() {
		ArrayList<Client> arr = mngr.getAllClients(getUserName());
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("MessageType", "NOTIFICATION");
		jsonObj.put("Notification_type", "registration");
		String jsonText = Parser.parse(jsonObj);

		if (arr == null)
			return;

		for (Client c : arr) {
			try {
				c.getConnection().sendToClient(jsonText);
			} catch (IOException e) {
				Logger.log(Level.WARNING, "CLIENT: IOException in sendRegistrationNotification");
			}
		}
	}
}
