package jade.input;

import jade.SiriusTheFox;
import jade.rendering.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double xPos, yPos, lastX, lastY;
    private double gameViewportX, gameViewportY, lastGameViewportX, lastGameViewportY;
    private double scrollX, scrollY;
    private final boolean[] mouseButtons = new boolean[9];
    private final boolean[] lastMouseButtons = new boolean[9];
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

        this.gameViewportX     = 0.0;
        this.gameViewportY     = 0.0;
        this.lastGameViewportX = 0.0;
        this.lastGameViewportY = 0.0;


        this.dragging = false;
    }

    public static void updateLastButtons() {
        System.arraycopy(get().mouseButtons, 0, get().lastMouseButtons, 0, get().mouseButtons.length);
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
        if (!SiriusTheFox.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()) clear();

        if (get().mouseButtonsDown > 0) {
            get().dragging = true;
        }

        // Receive actual values -- actual coordinates
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos  = xPos;
        get().yPos  = yPos;

        Vector2f viewportCoords = screenToGameViewport(new Vector2f((float) xPos, (float) yPos));
        get().lastGameViewportX = get().gameViewportX;
        get().lastGameViewportY = get().gameViewportY;
        get().gameViewportX = viewportCoords.x;
        get().gameViewportY = viewportCoords.y;
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

        get().gameViewportX     = 0.0;
        get().gameViewportY     = 0.0;
        get().lastGameViewportX = 0.0;
        get().lastGameViewportY = 0.0;

        get().mouseButtonsDown = 0;
        get().dragging = false;
        // Arrays.fill(get().mouseButtons, false);
    }

    private static Vector2f screenToWorld(Vector2f screenCoordinates, float width, float height) {
        Vector2f normalizedScreenCoordinates = new Vector2f(
                screenCoordinates.x / width,
                screenCoordinates.y / height
        );

        normalizedScreenCoordinates.mul(2.0f).sub(new Vector2f(1.0f, 1.0f)); // [-1, 1]
        Camera camera = SiriusTheFox.getCurrentScene().getCamera();
        Vector4f tmp = new Vector4f(normalizedScreenCoordinates.x, normalizedScreenCoordinates.y, 0.0f, 1.0f);
        Matrix4f inverseViewMatrix = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjectionMatrix = new Matrix4f(camera.getInverseProjection());

        // Undo everything
        tmp.mul(inverseViewMatrix.mul(inverseProjectionMatrix));

        return new Vector2f(tmp.x, tmp.y);
    }

    /**
     * Gets the world coordinates by using the formula:
     *     ScreenCoordinates = Position * ViewMatrix * ProjectionMatrix
     * Perfect to use, for example, for a shooting system.
     *
     * @param screenCoordinates The screen coordinates.
     * @return the actually world coordinates.
     */
    public static Vector2f screenToWorld(Vector2f screenCoordinates) {
        return screenToWorld(screenCoordinates, SiriusTheFox.getWindow().getWidth(), SiriusTheFox.getWindow().getHeight());
    }

    public static Vector2f screenToGameViewport(Vector2f screenCoordinates) {
        return screenToWorld(screenCoordinates, SiriusTheFox.getWindow().getMaxWidth(), SiriusTheFox.getWindow().getMaxHeight());
    }

    /**
     * Gets the screen coordinates by using the formula:
     *     WorldCoordinates = ScreenCoordinates * ViewMatrix^-1 * ProjectionMatrix^-1
     * @param worldCoordinates
     * @return the actually world coordinates.
     */
    public static Vector2f worldToScreen(Vector2f worldCoordinates) {
        Camera camera = SiriusTheFox.getCurrentScene().getCamera();
        Vector4f normalizedDeviceCoordsSpacePos = new Vector4f(worldCoordinates.x, worldCoordinates.y, 0.0f, 1.0f);
        Matrix4f viewMatrix = new Matrix4f(camera.getViewMatrix());
        Matrix4f projectionMatrix = new Matrix4f(camera.getProjectionMatrix());
        normalizedDeviceCoordsSpacePos.mul(projectionMatrix.mul(viewMatrix));

        Vector2f windowSpace = new Vector2f(normalizedDeviceCoordsSpacePos.x, normalizedDeviceCoordsSpacePos.y)
                .mul(1.0f / normalizedDeviceCoordsSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(SiriusTheFox.getWindow().getWidth(), SiriusTheFox.getWindow().getHeight()));

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
        Camera camera                    = SiriusTheFox.getCurrentScene().getCamera();
        Matrix4f inverseViewMatrix       = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjectionMatrix = new Matrix4f(camera.getInverseProjection());

        tmp.mul(inverseViewMatrix.mul(inverseProjectionMatrix));

        return new Vector2f(tmp.x, tmp.y);
    }

    private static float getXCoordinates(float width) {
        float currentX = getX() - get().gameViewportPos.x;

        // This will convert the currentX's range, [0, 1], to [-1, 1]
        currentX = (currentX / get().gameViewportSize.x) * width;

        return currentX;
    }

    private static float getYCoordinates(float height) {
        float currentY = getY() - get().gameViewportPos.y;

        // This will convert the currentX's range, [0, 1], to [-1, 1]
        currentY = height - ((currentY / get().gameViewportSize.y) * height); // Use '-' because ImGui has y coordinates flipped comparing to our project

        return currentY;
    }

    public static float getGameViewportX() {
        return getXCoordinates(SiriusTheFox.getWindow().getMaxWidth());
    }

    public static float getGameViewportY() {
        return getYCoordinates(SiriusTheFox.getWindow().getMaxHeight());
    }

    public static float getScreenX() {
        return getXCoordinates(SiriusTheFox.getWindow().getWidth());
    }

    public static float getScreenY() {
        return getYCoordinates(SiriusTheFox.getWindow().getHeight());
    }

    public static Vector2f getGameViewport() {
        return new Vector2f(getGameViewportX(), getGameViewportY());
    }

    public static Vector2f getScreen() {
        return new Vector2f(getScreenX(), getScreenY());
    }

    public static float getGameViewportDeltaX() {
        return (float) (get().lastGameViewportX - get().gameViewportX);
    }

    public static float getGameViewportDeltaY() {
        return (float) (get().gameViewportY - get().lastGameViewportY);
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

    public static boolean isMouseButtonPressed(int button) {
        if (button < get().mouseButtons.length) {
            return get().mouseButtons[button];
        }

        return false;
    }

    public static boolean isMouseButtonDown(int button) {
        if (button >= get().mouseButtons.length) return false;

        return get().mouseButtons[button] && !get().lastMouseButtons[button];
    }

    public static boolean isMouseButtonUp(int button) {
        if (button >= get().mouseButtons.length) return false;

        return !get().mouseButtons[button] && get().lastMouseButtons[button];
    }

    public static MouseListener get() {
        if (instance == null) instance = new MouseListener();
        return instance;
    }
}
