package communicationUtil;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Parser {
	public static String parse(JSONObject json) {
		String ret = JSONValue.toJSONString(json);
		ret += '\n';
		return ret;
	}

	public static JSONObject unparse(String str) {
		StringBuilder string = new StringBuilder(str);
		string.deleteCharAt(str.length() - 1);
		JSONObject jsonObj = (JSONObject) JSONValue.parse(string.toString());
		return jsonObj;
	}
}
