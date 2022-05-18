package sirius.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;
    private boolean[] lastKeys = new boolean[350];
    private boolean[] keys = new boolean[350];

    private KeyListener() {}

    public static void updateLastKeys() {
        for (int i = 0; i < get().keys.length; i++) {
            get().lastKeys[i] = get().keys[i];
        }
    }

    /**
     * Lê os valores do teclado
     * @param window Ponteiro
     * @param action Se alguma tecla foi pressionada
     * @param scancode
     * @param keycode ASCII decimal que aponta para um Char
     * @param mods Keybindings
     */
    public static void keyCallback(long window, int keycode, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (keycode < get().keys.length && keycode > 0) {
                get().keys[keycode] = true;
            }
        } else if (action == GLFW_RELEASE && keycode > 0) {
            if (keycode < get().keys.length) {
                get().keys[keycode] = false;
            }
        }
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
