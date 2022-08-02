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
     * @param keycode1 Should be the key we press first, like 'LEFT_CONTROL'
     * @param keycode2 Should be the key we press after, like 'D'
     * @return true if the bind is pressed.
     *
     * Example:
     *     if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D)) {
     *         doStuff();
     *     }
     */
    public static boolean isBindPressed(int keycode1, int keycode2) {
        return isKeyPressed(keycode1) && isKeyDown(keycode2);
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
}
