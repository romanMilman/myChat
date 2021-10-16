package clientSide;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import communicationUtil.Parser;

public class Message {

	private JSONObject jsonObj;
	private String type;

	public Boolean parse(Object msg) {
		String s = (String) msg;

		jsonObj = Parser.unparse(s);

		if (jsonObj == null)
			return false;

		type = (String) jsonObj.get("MessageType");
		return true;
	}

	public String getType() {
		return type;
	}

	public String getUserName() {
		return (String) jsonObj.get("UserName");
	}

	public String getResult() {
		return (String) jsonObj.get("Result");
	}

	public String getSourceUserName() {
		return (String) jsonObj.get("SourceUserName");
	}

	public String getNotificationType() {
		return (String) jsonObj.get("Notification_type");
	}

	public ArrayList<User> getFriends() {
		ArrayList<User> friendsList = new ArrayList<>();
		JSONArray jsonArray = (JSONArray) jsonObj.get("FriendList");

		if (jsonArray == null)
			return null;

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject explrObject = (JSONObject) jsonArray.get(i);

			User user = new User();
			user.setUsername((String) explrObject.get("Username"));
			user.setStatus((String) explrObject.get("Status"));

			friendsList.add(user);
		}
		return friendsList;
	}

	public ArrayList<ChatMessage> getChat() {
		ArrayList<ChatMessage> chatList = new ArrayList<ChatMessage>();
		JSONArray jsonArray = (JSONArray) jsonObj.get("Chat");

		if (jsonArray == null)
			return null;

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject explrObject = (JSONObject) jsonArray.get(i);

			ChatMessage m = new ChatMessage(explrObject.get("Date"), explrObject.get("Text"), explrObject.get("Me"));
			chatList.add(m);
		}
		return chatList;
	}
}
