package jade.input;

import jade.Window;
import jade.rendering.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double xPos, yPos, lastX, lastY;
    private double scrollX, scrollY;
    private final boolean[] mouseButtons = new boolean[9];
    private boolean dragging;

    private int mouseButtonsDown;

    // Game viewport
    private Vector2f gameViewportPos  = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        // Definir os valores
        this.xPos     = 0.0;
        this.yPos     = 0.0;
        this.lastX    = 0.0;
        this.lastY    = 0.0;
        this.scrollX  = 0.0;
        this.scrollY  = 0.0;
        this.dragging = false;
    }

    /**
     * To get the mouse coordinates
     * We will use get().lastX, get().lastY()... To, in the future, we may call
     * MouseListener.mousePositionCallback() instead of MouseListener.get().mousePositionCallback(),
     *
     * @param xPos (Position) x mouse's actual coordinates
     * @param yPos (Position) y mouse's actual coordinates
     */
    public static void mousePosCallback(long window, double xPos, double yPos) {
        if (!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()) clear();

        if (get().mouseButtonsDown > 0) {
            get().dragging = true;
        }

        // Receive actual values -- actual coordinates
        get().lastX = xPos;
        get().lastY = yPos;
        get().xPos = xPos;
        get().yPos = yPos;
    }

    /**
     * Para o jogo reconhecer keycodes
     * @param window Ponteiro da janela
     * @param button Botão pressionado
     * @param action Verificar se botão foi pressionado
     * @param mods Para keybindings
     */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            // Verificar se "temos as teclas todas"
            if (button < get().mouseButtons.length) {
                get().mouseButtonsDown++;
                get().mouseButtons[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < get().mouseButtons.length) {
                get().mouseButtonsDown--;
                get().mouseButtons[button] = false;

                // Como não clicamos em nenhum botão, sabemos que também não há dragging
                get().dragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double scrollX, double scrollY) {
        get().scrollX = scrollX;
        get().scrollY = scrollY;
    }

    public static void endFrame() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
    }

    public static void clear() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().xPos    = 0.0;
        get().yPos    = 0.0;
        get().lastX   = 0.0;
        get().lastY   = 0.0;

        get().mouseButtonsDown = 0;
        get().dragging = false;
        // Arrays.fill(get().mouseButtons, false);
    }

    /**
     * Gets the world coordinates by using the formula:
     *     ScreenCoordinates = Position * ViewMatrix * ProjectionMatrix
     * @param screenCoordinates
     * @return the actually world coordinates.
     */
    public static Vector2f screenToWorld(Vector2f screenCoordinates) {
        Vector2f normalizedScreenCoordinates = new Vector2f(
                screenCoordinates.x / Window.getWidth(),
                screenCoordinates.y / Window.getHeight()
        );

        normalizedScreenCoordinates.mul(2.0f).sub(new Vector2f(1.0f, 1.0f)); // [-1, 1]
        Camera camera = Window.getCurrentScene().getCamera();
        Vector4f tmp = new Vector4f(normalizedScreenCoordinates.x, normalizedScreenCoordinates.y, 0.0f, 1.0f);
        Matrix4f inverseViewMatrix = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjectionMatrix = new Matrix4f(camera.getInverseProjection());

        // Undo everything
        tmp.mul(inverseViewMatrix.mul(inverseProjectionMatrix));

        return new Vector2f(tmp.x, tmp.y);
    }

    /**
     * Gets the screen coordinates by using the formula:
     *     WorldCoordinates = ScreenCoordinates * ViewMatrix^-1 * ProjectionMatrix^-1
     * @param worldCoordinates
     * @return the actually world coordinates.
     */
    public static Vector2f worldToScreen(Vector2f worldCoordinates) {
        Camera camera = Window.getCurrentScene().getCamera();
        Vector4f normalizedDeviceCoordsSpacePos = new Vector4f(worldCoordinates.x, worldCoordinates.y, 0.0f, 1.0f);
        Matrix4f viewMatrix = new Matrix4f(camera.getViewMatrix());
        Matrix4f projectionMatrix = new Matrix4f(camera.getProjectionMatrix());
        normalizedDeviceCoordsSpacePos.mul(projectionMatrix.mul(viewMatrix));

        Vector2f windowSpace = new Vector2f(normalizedDeviceCoordsSpacePos.x, normalizedDeviceCoordsSpacePos.y)
                .mul(1.0f / normalizedDeviceCoordsSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

        return windowSpace;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static double getXD() {
        return get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static Vector2f getWorld() {
        float currentX = getX() - get().gameViewportPos.x;
        float currentY = getY() - get().gameViewportPos.y;

        // This will convert the currentX's and currentY's range, [0, 1], to [-1, 1]
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f); // Use '-' because ImGui has y coordinates flipped comparing to our project

        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1); // 1 IS VERY IMPORTANT TO MAINTAIN THE INTEGRITY OF MATRIX MULTIPLICATION

        // See explanation of this in Camera.java file in its constructor method.
        Camera camera                    = Window.getCurrentScene().getCamera();
        Matrix4f inverseViewMatrix       = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjectionMatrix = new Matrix4f(camera.getInverseProjection());

        tmp.mul(inverseViewMatrix.mul(inverseProjectionMatrix));

        return new Vector2f(tmp.x, tmp.y);
    }

    public static float getScreenX() {
        float currentX = getX() - get().gameViewportPos.x;

        // This will convert the currentX's range, [0, 1], to [-1, 1]
        // TODO: 05/01/2022 Change 1920.0f to the size of the monitor
        currentX = (currentX / get().gameViewportSize.x) * 1920.0f;

        return currentX;
    }

    public static float getScreenY() {
        float currentY = getY() - get().gameViewportPos.y;

        // This will convert the currentX's range, [0, 1], to [-1, 1]
        // TODO: 05/01/2022 Change 1080.0f to the size of the monitor
        currentY = 1080.0f - ((currentY / get().gameViewportSize.y) * 1080.0f); // Use '-' because ImGui has y coordinates flipped comparing to our project

        return currentY;
    }

    public static Vector2f getScreen() {
        return new Vector2f(getScreenX(), getScreenY());
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().dragging;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtons.length) {
            return get().mouseButtons[button];
        }

        return false;
    }

    public static MouseListener get() {
        if (instance == null) instance = new MouseListener();
        return instance;
    }
}
