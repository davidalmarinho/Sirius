package jade;

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
            if (keycode < get().keys.length) {
                get().keys[keycode] = true;
            }
        } else if (action == GLFW_RELEASE) {
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

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }

        return instance;
    }
}
