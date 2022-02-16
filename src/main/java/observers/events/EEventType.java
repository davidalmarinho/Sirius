package observers.events;

public enum EEventType {
    GAME_ENGINE_START_PLAY,
    GAME_ENGINE_STOP_PLAY,
    SAVE_LEVEL,
    LOAD_LEVEL,
    // For the users to create their own events
    USER_EVENT,
}
