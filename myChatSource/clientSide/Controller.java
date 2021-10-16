package clientSide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;

public class Controller implements ControllerCommAPI, ControllerUiAPI {
	// temp var
	Timer timer;

	// VARIABLES
	private UIManager uiMngr;
	private CommunicationManager comMngr;

	private Window currWindow = Window.LOGIN_WINDOW;
	private Status status = Status.DISCONNECTED;

	public void init(UIManager uiMngr, CommunicationManager comMngr) {
		this.uiMngr = uiMngr;
		this.comMngr = comMngr;

		// log
		Logger.log(Level.DEBUG, "Controller: controller initialized");
		System.out.println("Controller: controller initialized");
	}

	public void run() {
		// log
		Logger.log(Level.DEBUG, "Controller: starting communication manager");
		System.out.println("Controller: starting communication manager");

		comMngr.Start();

		uiMngr.openLoginWindow();

	}

	public void stop() {
		// log
		Logger.log(Level.DEBUG, "Controller: stoping communication manager");
		System.out.println("Controller: stoping communication manager");

		comMngr.End();

		// temp for ping
		if (timer != null)
			timer.cancel();
	}

	public void setWindow(Window window) {
		currWindow = window;
	}

	public Window getWindow() {
		return currWindow;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	// ####################### ControllerCommAPI ####################
	@Override
	public void serverStatus(Status status) {
		// log
		Logger.log(Level.DEBUG, "Controller: server status is: " + status);
		System.out.println("Controller: server status is: " + status);
		
		setStatus(status);
		uiMngr.updateStatus(status);
	}

	@Override
	public void newMessageArrived(String sourceUserName, String notificationType) {
		// log
		Logger.log(Level.DEBUG, "Controller: new message recived from: " + sourceUserName);
		System.out.println("Controller: new message recived from: " + sourceUserName);
		
		if (notificationType.equals("text"))
			uiMngr.updateChat(sourceUserName);
		if (notificationType.equals("registration")) {
			comMngr.sendFriendsListRequest();
		}
	}

	@Override
	public void userFound() {
	}

	@Override
	public void friendsList(ArrayList<User> list) {
		if ((list != null) && (currWindow == Window.CHAT_WINDOW)) {
			uiMngr.updateFriendsList(list);
		}
	}

	@Override
	public void authorizationResult(String result) {
		if (result.equals("true") && (currWindow == Window.LOGIN_WINDOW)) {
			uiMngr.openChatWindow();
			currWindow = Window.CHAT_WINDOW;
			comMngr.sendFriendsListRequest();

		} else {
			uiMngr.showError();
		}
	}

	@Override
	public void registrationResult(String result) {
		if (result.equals("true") && (currWindow == Window.REGISTER_WINDOW)) {
			uiMngr.openChatWindow();
			currWindow = Window.CHAT_WINDOW;
			comMngr.sendFriendsListRequest();
		} else {
			uiMngr.showError();
		}
	}

	public void chatArrived(ArrayList<ChatMessage> list) {
		if (currWindow == Window.CHAT_WINDOW) {
			uiMngr.buildChatList(list);
		}
	}

	public void connectionLost() {
		// log
		Logger.log(Level.DEBUG, "Controller: connection lost");
		System.out.println("Controller: connection lost");

		setWindow(Window.LOGIN_WINDOW);
		setStatus(Status.DISCONNECTED);
		uiMngr.openLoginWindow();
		uiMngr.updateStatus(Status.DISCONNECTED);
	}
	// ####################### ControllerUiAPI ######################

	@Override
	public void putMessage(String msg, String destinationUserName) {
		comMngr.sendText(msg, destinationUserName);

	}

	@Override
	public void register(String username, String password) {
		comMngr.sendRegistrationRequest(username, password);
	}

	@Override
	public void unregister() {
		// TODO Auto-generated method stub

	}

	@Override
	public void findUser() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFriend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void login(String username, String password) {
		comMngr.sendAuthorizationRequest(username, password);
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestAllChat(String destinationUserName) {
		comMngr.sendAllChatRequest(destinationUserName);

	}

	public void openWindow() {
		if (currWindow == Window.LOGIN_WINDOW) {
			uiMngr.openLoginWindow();
			uiMngr.updateStatus(status);
		}
		if (currWindow == Window.REGISTER_WINDOW) {
			uiMngr.openRegisterWindow();
			uiMngr.updateStatus(status);
		}
	}
	// ##############################################

	public void commMngrFailed() {
		uiMngr.fatalError();
	}
}
