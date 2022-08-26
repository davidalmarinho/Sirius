package observers.events;

public enum Events {
    GAME_ENGINE_START_PLAY,
    GAME_ENGINE_STOP_PLAY,
    SAVE_LEVEL,
    LOAD_LEVEL,
    EXPORT_GAME,
    // For the users to create their own events
    USER_EVENT,
}
