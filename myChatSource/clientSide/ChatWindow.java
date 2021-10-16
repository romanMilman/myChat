package clientSide;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatWindow {

	private final int MAX_INPUT = 1024;

	private UIManager uiMngr;
	private HBox chatHBox;
	private Stage primaryStage;
	private Scene scene;

	private HashMap<String, Circle> notificationMap = new HashMap<>();

	private String currUser;

	@FXML
	private VBox contactsVBox;

	@FXML
	private VBox chatVBox;

	@FXML
	private Button chatSendButton;

	@FXML
	private TextArea chatTextArea;

	@FXML
	private ScrollPane scroll;

	public void init(UIManager uiMngr, HBox chatHBox, Stage primaryStage) {
		this.chatHBox = chatHBox;
		this.primaryStage = primaryStage;
		this.uiMngr = uiMngr;
	}

	@SuppressWarnings("unchecked")
	public void showWindow() {
		// log
		Logger.log(Level.DEBUG, "ChatWindow: showing window");
		System.out.println("ChatWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(chatHBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "ChatWindow: exception in showWindow");
				System.out.println("ChatWindow: exception in showWindow");
			}

			// set Max number of character that can be typed in a single message
			chatTextArea.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));

			// Auto scroll down in any change of chat scroll view
			chatVBox.heightProperty().addListener(new ChangeListener() {

				@Override
				public void changed(ObservableValue observable, Object oldvalue, Object newValue) {

					scroll.setVvalue((Double) newValue);
				}
			});

			primaryStage.setScene(scene);
			primaryStage.show();
		});
	}

	@SuppressWarnings("static-access")
	public void buildFriendsList(ArrayList<User> list) {
		// log
		Logger.log(Level.DEBUG, "ChatWindow: building friends list");
		System.out.println("ChatWindow: building friends list");

		Platform.runLater(() -> {
			contactsVBox.getChildren().clear();

			if (list == null)
				return;

			for (User user : list) {

				HBox contactHBox = new HBox();
				VBox circlesVBox = new VBox();

				Button b = new Button();
				b.setText(user.getUsername());
				b.setAlignment(Pos.CENTER);
				b.setFont(Font.font(20));
				b.setMaxWidth(Double.MAX_VALUE);
				b.setMaxHeight(Double.MAX_VALUE);

				contactHBox.setHgrow(b, Priority.ALWAYS);

				b.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						if (currUser == b.getText())
							return;
						uiMngr.getController().requestAllChat(b.getText());
						currUser = b.getText();
						notificationMap.get(currUser).setFill(Paint.valueOf("GREY"));
					}
				});

				// status circles
				Circle statusCircle = new Circle();
				statusCircle.setRadius(5);
				statusCircle.setTranslateY(15);
				if (user.getStatus())
					statusCircle.setFill(Paint.valueOf("GREEN"));
				else
					statusCircle.setFill(Paint.valueOf("RED"));

				// notification circles
				Circle notifyCircle = new Circle();
				if (!notificationMap.containsKey(user.getUsername())) {
					notifyCircle.setRadius(5);
					notifyCircle.setTranslateY(10);
					notifyCircle.setFill(Paint.valueOf("GREY"));
					notificationMap.put(user.getUsername(), notifyCircle);
				} else {
					notifyCircle = notificationMap.get(user.getUsername());
				}

				circlesVBox.getChildren().add(notifyCircle);
				circlesVBox.getChildren().add(statusCircle);
				circlesVBox.setPadding(new Insets(1, 1, 1, 4));

				contactHBox.getChildren().add(b);
				contactHBox.getChildren().add(circlesVBox);

				contactsVBox.getChildren().add(contactHBox);
			}
		});
	}

	public void buildChatList(ArrayList<ChatMessage> list) {
		// log
		Logger.log(Level.DEBUG, "ChatWindow: building chat list");
		System.out.println("ChatWindow: building chat list");

		Platform.runLater(() -> {
			chatVBox.getChildren().clear();

			if (list == null)
				return;

			for (ChatMessage user : list) {
				Label l = new Label();
				String text = fixLongMsgs(user.getText());
				l.setText(user.getDate() + " " + user.getTime() + "\n" + text);

				if (user.isMe())
					l.setAlignment(Pos.CENTER_RIGHT);
				else {
					l.setAlignment(Pos.CENTER_LEFT);
					l.setStyle("-fx-background-color: #ededed;");
				}

				l.setFont(Font.font(18));
				l.setMaxWidth(Double.MAX_VALUE);
				l.setMaxHeight(Double.MAX_VALUE);
				l.setMinHeight(Region.USE_PREF_SIZE);

				chatVBox.getChildren().add(l);

			}
		});
	}

	// TODO maybe show this msg in chat with PENDING status until server verifys.
	@FXML
	public void onSendButton(ActionEvent event) {
		String msg = chatTextArea.getText();
		String text = fixLongMsgs(msg);

		if (text.length() == 0)
			return;

		if (currUser != null) {
			chatTextArea.clear();

			// show my sent msg
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formatDateTime = now.format(format);

			ChatMessage chatMsg = new ChatMessage(formatDateTime, text, "true");

			Label l = new Label();
			l.setText(chatMsg.getDate() + " " + chatMsg.getTime() + "\n" + text);
			l.setAlignment(Pos.CENTER_RIGHT);
			l.setFont(Font.font(18));
			l.setMaxWidth(Double.MAX_VALUE);
			l.setMaxHeight(Double.MAX_VALUE);
			l.setMinHeight(Region.USE_PREF_SIZE);

			chatVBox.getChildren().add(l);

			uiMngr.getController().putMessage(text, currUser);

		}
	}

	public void updateChat(String sourceUserName) {
		// log
		Logger.log(Level.DEBUG, "ChatWindow: updating chat");
		System.out.println("ChatWindow: updating chat");

		Platform.runLater(() -> {
			if (currUser != null && currUser.equals(sourceUserName)) {
				uiMngr.getController().requestAllChat(currUser);
			} else {
				notificationMap.get(sourceUserName).setFill(Paint.valueOf("PURPLE"));
			}
		});
	}

	// TODO find words
	private String fixLongMsgs(String msg) {
		String fixed = "";
		String message = shortenMsgs(msg);

		for (int i = 0; i < message.length(); i++) {
			if (i % 50 == 0 && i != 0) {
				fixed += '\n';
			}
			fixed += message.charAt(i);
		}

		return fixed;
	}

	private String shortenMsgs(String msg) {
		StringBuilder message = new StringBuilder(msg);

		for (int i = 0; i < message.length();) {
			if (message.charAt(i) != '\n')
				break;
			else
				message.deleteCharAt(i);
		}

		for (int i = message.length() - 1; i >= 0; i--) {
			if (message.charAt(i) != '\n')
				return message.toString();
			else
				message.deleteCharAt(i);
		}

		return message.toString();
	}
}
