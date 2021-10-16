package serverSide;

import org.json.simple.JSONObject;

import communicationUtil.Parser;

public class Message {

	// -----------------------VARIABLES

	private JSONObject jsonObj;
	private String type;

	// -----------------------CUSTOM FUNC

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

	public String getPassword() {
		return (String) jsonObj.get("Password");
	}

	public String getDestinationUserName() {
		return (String) jsonObj.get("DestinationUserName");
	}

	public String getText() {
		return (String) jsonObj.get("Text");
	}

	public String getTime() {
		return (String) jsonObj.get("Time");
	}

	public String getDate() {
		return (String) jsonObj.get("Date");
	}
}
