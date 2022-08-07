package sirius.input;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean[] lastKeys = new boolean[350];
    private boolean[] keys = new boolean[350];
    private char lastPressedCharacter = '\0';
    private int mods;
    private static boolean capsLock;

    static {
        capsLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    }

    private KeyListener() {}

    public static void updateLastKeys() {
        for (int i = 0; i < get().keys.length; i++) {
            get().lastKeys[i] = get().keys[i];
        }
    }

    /**
     * Read values from keyboard
     * @param window The window pointer
     * @param action If is has been any key pressed
     * @param scancode
     * @param keycode ASCII decimal that points to a char
     * @param mods Keybindings
     */
    public static void keyCallback(long window, int keycode, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (keycode < get().keys.length && keycode > 0) {
                get().keys[keycode] = true;
            }

            get().mods = mods;

            // Set caps lock
            if (keycode == GLFW_KEY_CAPS_LOCK) {
                capsLock = !capsLock;
            }

        } else if (action == GLFW_RELEASE && keycode > 0) {
            if (keycode < get().keys.length) {
                get().keys[keycode] = false;
            }

            // TODO: 02/08/2022 See better about mods
            get().mods = mods;
        }
    }

    public static void characterCallback(long window, int codepoint) {
        get().lastPressedCharacter = (char) codepoint;
    }
    public static boolean isKeyPressed(int keycode) {
        if (keycode < get().keys.length) {
            return get().keys[keycode];
        }

        return false;
    }

    public static boolean isKeyDown(int keycode) {
        if (keycode < get().keys.length) {
            return get().keys[keycode] && !get().lastKeys[keycode];
        }

        return false;
    }

    public static boolean isKeyUp(int keycode) {
        if (keycode < get().keys.length) {
            return !get().keys[keycode] && get().lastKeys[keycode];
        }

        return false;
    }

    /**
     * Automatic binds 2 keys.
     *
     * @param modKey Should be the key we press first, like 'LEFT_CONTROL'
     * @param key Should be the key we press after, like 'C'
     * @return true if the bind is pressed.
     *
     * Example:
     *     if (KeyListener.isBindDown(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_C)) {
     *         copy();
     *     }
     */
    public static boolean isBindDown(int modKey, int key) {
        return isKeyPressed(modKey) && isKeyDown(key);
    }

    /**
     * Automatic binds 3 keys.
     *
     * @param modKey1 Should be a mod key like 'LEFT_CONTROL'
     * @param modKy2 Should be a mod key like 'LEFT_SHIFT'
     * @param key Should be the key we press, like 'DEL'
     * @return true if the bind is pressed.
     *
     * Example:
     *     if (KeyListener.isBindDown(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_LEFT_SHIFT, GLFW_KEY_ESC)) {
     *         openWindowsTaskManager();
     *     }
     */
    public static boolean isBindDown(int modKey1, int modKy2, int key) {
        return isKeyPressed(modKey1) && isKeyPressed(modKy2) && isKeyDown(key);
    }

    /**
     * Automatic binds 4 keys.
     * @return true if the bind is pressed.
     * See {@link KeyListener#isBindDown(int, int, int)} docs for more information.
     */
    public static boolean isBindDown(int modKey1, int modKy2, int modKey3, int key) {
        return isKeyPressed(modKey1) && isKeyPressed(modKy2) && isKeyPressed(modKey3) && isKeyDown(key);
    }

    /**
     * Automatic binds 2 keys.
     * @return true if the bind is being holding.
     * See {@link KeyListener#isBindDown(int, int)} example in docs.
     */
    public static boolean isBindPressed(int modKey, int key) {
        return isKeyPressed(modKey) && isKeyPressed(key);
    }

    /**
     * Automatic binds 3 keys.
     * @return true if the bind is being holding.
     * See {@link KeyListener#isBindDown(int, int, int)} example in docs.
     */
    public static boolean isBindPressed(int modKey1, int modKey2, int key) {
        return isKeyPressed(modKey1) && isKeyPressed(modKey2) && isKeyPressed(key);
    }

    /**
     * Automatic binds 4 keys.
     * @return true if the bind is holding.
     * See {@link KeyListener#isBindDown(int, int)} example in docs.
     */
    public static boolean isBindPressed(int modKey1, int modKy2, int modKey3, int key) {
        return isKeyPressed(modKey1) && isKeyPressed(modKy2) && isKeyPressed(modKey3) && isKeyPressed(key);
    }

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }

        return instance;
    }

    public static char getPressedKey() {
        char c = get().lastPressedCharacter;
        get().lastPressedCharacter = '\0';
        return c;
    }

    public static boolean isAnyKeyPressed() {
        boolean pressedKey = false;
        for (boolean key : get().keys) {
            if (key) {
                pressedKey = true;
                break;
            }
        }

        return pressedKey;
    }

    public static boolean isAnyKeyDown() {
        for (int i = 0; i < get().keys.length; i++) {
            if (get().keys[i]) {
                if (!get().lastKeys[i]) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isAnyKeyReleased() {
        for (int i = 0; i < get().lastKeys.length; i++) {
            if (get().lastKeys[i]) {
                if (!get().keys[i]) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isAnyModKeyPressed() {
        return isKeyPressed(GLFW_KEY_LEFT_SUPER) || isKeyPressed(GLFW_KEY_RIGHT_SUPER)
                || isKeyPressed(GLFW_KEY_MENU)
                || isKeyPressed(GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW_KEY_RIGHT_SHIFT)
                || isKeyPressed(GLFW_KEY_LEFT_ALT) || isKeyPressed(GLFW_KEY_RIGHT_ALT)
                || isKeyPressed(GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isAnyModKeyDown() {
        return isKeyDown(GLFW_KEY_LEFT_SUPER) || isKeyDown(GLFW_KEY_RIGHT_SUPER)
                || isKeyDown(GLFW_KEY_MENU)
                || isKeyDown(GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW_KEY_RIGHT_SHIFT)
                || isKeyDown(GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW_KEY_RIGHT_ALT)
                || isKeyDown(GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW_KEY_RIGHT_CONTROL);
    }
}
