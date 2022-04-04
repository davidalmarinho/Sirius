package jade.input;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class Input {
    public static void updateEvents() {
        KeyListener.updateLastKeys();
        MouseListener.updateLastButtons();

        // Load the event of the Keyboard, the mouse...
        glfwPollEvents();
    }
}
