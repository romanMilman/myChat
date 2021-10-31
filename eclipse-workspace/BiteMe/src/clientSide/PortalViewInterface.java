package clientSide;

import org.json.simple.JSONObject;

public interface PortalViewInterface {
	public void ShowScreen(JSONObject descriptor);

	public void init();

	public void ready();

	public ComController getComController();
}
