package observers.events;

public class Event {
    public EEventType type;

    /**
     * Constructor for Event class.
     */
    public Event(EEventType eventType) {
        this.type = eventType;
    }

    /**
     * Constructor for Event class.
     * Created for others users purposes. Because, by this way, others users may create
     * their own event types.
     */
    public Event() {
        this(EEventType.USER_EVENT);
    }
}
