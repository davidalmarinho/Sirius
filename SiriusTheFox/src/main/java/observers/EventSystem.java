package observers;

import gameobjects.GameObject;
import observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    private static List<Observer> observerList = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observerList.add(observer);
    }

    /**
     * Calls {@link Observer#onNotify(GameObject, Event)} method.
     *
     * @param gameObject Notify this game object.
     * @param event The event of the game object.
     */
    public static void notify(GameObject gameObject, Event event) {
        observerList.forEach(observer -> observer.onNotify(gameObject, event));
    }
}
