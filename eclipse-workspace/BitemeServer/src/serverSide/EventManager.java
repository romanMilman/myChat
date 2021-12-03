package serverSide;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class EventManager {

	private HashMap<String, ArrayList<EventListener>> listeners = new HashMap<>();

	private static EventManager instance = null;

	public static EventManager getInstance() {
		if (instance == null)
			instance = new EventManager();
		return instance;
	}

	public void subscribe(String event, EventListener l) {
		synchronized (listeners) {
			ArrayList<EventListener> listenerArray = listeners.get(event);

			if (listenerArray == null) {
				listenerArray = new ArrayList<EventListener>();
				listeners.put(event, listenerArray);
			}
			listenerArray.add(l);
		}
	}

	public void unsubscribe(String event, EventListener l) {
		synchronized (listeners) {
			ArrayList<EventListener> listenerArray = listeners.get(event);

			listenerArray.remove(l);
		}
	}

	public void notify(String event, JSONObject json) {
		synchronized (listeners) {
			ArrayList<EventListener> listenerArray = listeners.get(event);

			if (listenerArray == null)
				return;

			for (EventListener l : listenerArray) {
				l.HandleEvent(json);
			}
		}
	}
}
