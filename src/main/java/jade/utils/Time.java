package jade.utils;

public class Time {
    public static float timeStarted = System.nanoTime();

    /**
     * Gets the time since the app has started
     * @return the time since the app has started
     */
    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1e-9);
    }
}
