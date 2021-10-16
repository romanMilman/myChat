package clientSide;

import java.util.ArrayList;
import java.util.HashMap;

public interface ControllerCommAPI {

	public void serverStatus(Status status);

	public void newMessageArrived(String destinationUserName, String notificationType);

	public void userFound();

	public void friendsList(ArrayList<User> list);

	public void authorizationResult(String result);

	public void registrationResult(String result);

	public void chatArrived(ArrayList<ChatMessage> arrayList);

	public void connectionLost();

	public void commMngrFailed();

}
