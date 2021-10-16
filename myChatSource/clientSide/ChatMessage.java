package clientSide;

public class ChatMessage {
	private String date;
	private String time;
	private String text;
	private Boolean me;

	public ChatMessage(Object date, Object text, Object me) {
		this.text = (String) text;
		this.date = ((String) date).substring(0, 10);
		time = ((String) date).substring(11);

		if (((String) me).equals("true"))
			this.me = true;
		else
			this.me = false;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public String getText() {
		return text;
	}

	public Boolean isMe() {
		return me;
	}
}
