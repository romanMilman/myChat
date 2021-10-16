package clientSide;

public interface ControllerUiAPI {
	public void putMessage(String msg, String destinationUserName);

	public void register(String username, String password);

	public void unregister();

	public void findUser();

	public void addFriend();

	public void login(String username, String password);

	public void logout();

	public void requestAllChat(String destinationUserName);

	public void setWindow(Window window);

	public Window getWindow();

	public void setStatus(Status status);

	public Status getStatus();

	public void openWindow();
}
