package clientSide;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import communicationUtil.Logger;
import communicationUtil.Parser;
import communicationUtil.Logger.Level;
import ocsf.client.AbstractClient;

public class CommunicationManager extends AbstractClient {

	private final int MAX_STREAM_SIZE = 30000;

	private ControllerCommAPI controller;
	private Message message;
	private Timer timer;
	private String communicationBuffer = "";

	private Boolean dnsOk = true;

	public CommunicationManager(String host, int port, ControllerCommAPI controller) {
		super(host, port);
		this.controller = controller;
		message = new Message();

		if (host.equals(""))
			dnsOk = false;
	}

	// TODO maybe connection failed handle this
	public void Start() {
		if (!dnsOk) {

			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					controller.commMngrFailed();
				};
			};

			timer.schedule(task, 2000);

			return;
		}
		// log
		Logger.log(Level.DEBUG, "ComMngr: opening connection");
		System.out.println("ComMngr: opening connection");

		try {
			openConnection();
		} catch (IOException e) {
			System.out.println(e.toString());
			Logger.log(Level.WARNING, e.toString());

			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					// log
					Logger.log(Level.DEBUG, "trying to connect...");
					System.out.println("trying to connect...");

					Start();
				};
			};

			timer.schedule(task, 5000);

		}
	}

	public void End() {
		try {
			// log
			Logger.log(Level.DEBUG, "ComMngr: closing connection");
			Logger.log(Level.DEBUG, "ComMngr: closing timer");
			System.out.println("ComMngr: closing connection");
			System.out.println("ComMngr: closing timer");

			if (timer != null) {
				timer.cancel();
				Logger.log(Level.DEBUG, "ComMngr: timer closed");
				System.out.println("ComMngr: timer closed");
			}

			closeConnection();
			Logger.log(Level.DEBUG, "ComMngr: connection closed");
			System.out.println("ComMngr: connection closed");
		} catch (IOException e) {
			// log
			Logger.log(Level.WARNING, "ComMngr: failed to disconnect");
			System.out.println("ComMngr: failed to disconnect");
		}
	}

	// TODO handle TCP stream
	@Override
	protected void handleMessageFromServer(Object msg) {
		onHandleMsg(msg);
	}

	public void processMsg(Object msg) {
		if (!message.parse(msg)) {
			Logger.log(Level.DEBUG, "ComMngr: JSON failed: " + msg);
			System.out.println("ComMngr: JSON failed: " + msg);
			return;
		}

		// log
		Logger.log(Level.DEBUG, "ComMngr: Message recived: " + (String) msg);
		System.out.println("ComMngr: Message recived: " + (String) msg);

		switch (message.getType()) {
		case "AUTORIZATION_REQUEST":
			controller.authorizationResult(message.getResult());
			break;
		case "REGISTRATION_REQUEST":
			controller.registrationResult(message.getResult());
			break;
		case "NOTIFICATION":
			controller.newMessageArrived(message.getSourceUserName(), message.getNotificationType());
			break;
		case "ALL_CHAT_REQUEST":
			controller.chatArrived(message.getChat());
			break;
		case "NEW_CHAT_REQURST":
			break;
		case "FRIEND_LIST_REQUEST":
			controller.friendsList(message.getFriends());
			break;
		case "FRIEND_STATUS_REQUEST":
			break;
		case "PING":
			Logger.log(Level.DEBUG, "ComMnger: recieved ping from server");
			System.out.println("ComMnger: recieved ping from server");
			sendPing();
			break;
		default:
			Logger.log(Level.DEBUG, "ComMngr: this message is not an option");
			System.out.println("ComMngr: this message is not an option");
			break;
		}
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

	@Override
	protected void connectionEstablished() {
		// log
		Logger.log(Level.DEBUG, "ComMnger: connection Established");
		System.out.println("ComMnger: connection Established");

		controller.serverStatus(Status.CONNECTED);
	}

	@Override
	protected void connectionException(Exception exception) {
		// log
		Logger.log(Level.WARNING, "ComMngr: connection exception");
		System.out.println("ComMngr: connection exception");

		timer = new Timer();
		TimerTask task = new TimerTask() {

			public void run() {
				// log
				Logger.log(Level.DEBUG, "trying to connect...");
				System.out.println("trying to connect...");

				Start();
			};
		};

		controller.connectionLost();
		timer.schedule(task, 5000);
	}

	@SuppressWarnings("unchecked")
	public void sendAuthorizationRequest(String username, String password) {

		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "AUTORIZATION_REQUEST");
		jsonObj.put("UserName", username);
		jsonObj.put("Password", password);

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending authorization request, msg: " + jsonText);
		System.out.println("ComMngr: Sending authorization request, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendAuthorizationRequest");
			System.out.println("ComMngr: exception in sendAuthorizationRequest");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendRegistrationRequest(String username, String password) {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "REGISTRATION_REQUEST");
		jsonObj.put("UserName", username);
		jsonObj.put("Password", password);

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending registration request, msg: " + jsonText);
		System.out.println("ComMngr: Sending registration request, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendRegistrationRequest");
			System.out.println("ComMngr: exception in sendRegistrationRequest");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendFriendsListRequest() {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "FRIEND_LIST_REQUEST");

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending friends list request, msg: " + jsonText);
		System.out.println("ComMngr: Sending friends list request, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendFriendsListRequest");
			System.out.println("ComMngr: exception in sendFriendsListRequest");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendFriendsStatusRequest() {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "FRIEND_STATUS_REQUEST");

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending friends status request, msg: " + jsonText);
		System.out.println("ComMngr: Sending friends status request, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendFriendsStatusRequest");
			System.out.println("ComMngr: exception in sendFriendsStatusRequest");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendAllChatRequest(String destinationUserName) {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "ALL_CHAT_REQUEST");
		jsonObj.put("DestinationUserName", destinationUserName);

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending all chat request, msg: " + jsonText);
		System.out.println("ComMngr: Sending all chat request, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendAllChatRequest");
			System.out.println("ComMngr: exception in sendAllChatRequest");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendText(String text, String destinationUserName) {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "TEXT");
		jsonObj.put("Text", text);
		jsonObj.put("DestinationUserName", destinationUserName);

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending text, msg: " + jsonText);
		System.out.println("ComMngr: Sending text, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendText");
			System.out.println("ComMngr: exception in sendText");
		}
	}

	@SuppressWarnings("unchecked")
	public void sendPing() {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("MessageType", "PING");

		String jsonText = Parser.parse(jsonObj);

		// log
		Logger.log(Level.DEBUG, "ComMngr: Sending ping, msg: " + jsonText);
		System.out.println("ComMngr: Sending ping, msg: " + jsonText);

		try {
			sendToServer(jsonText);
		} catch (IOException e) {
			Logger.log(Level.WARNING, "ComMngr: exception in sendPing");
			System.out.println("ComMngr: exception in sendPing");
		}
	}
}
