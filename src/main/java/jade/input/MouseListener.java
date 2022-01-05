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
    private double xPos, yPos, lastXPos, lastYPos;
    private double scrollX, scrollY;
    private final boolean[] mouseButtons = new boolean[9];
    private boolean dragging;

    // Game viewport
    private Vector2f gameViewportPos  = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        // Definir os valores
        this.xPos     = 0.0;
        this.yPos     = 0.0;
        this.lastXPos = 0.0;
        this.lastYPos = 0.0;
        this.scrollX  = 0.0;
        this.scrollY  = 0.0;
        this.dragging = false;
    }

    /**
     * Para receber as coordenadas do rato
     * Vamos usar get().lastX, get().lastY()... Para futuramente pudermos chamar
     * MouseListener.mousePositionCallback() ao invés de MouseListener.get().mousePositionCallback(),
     * pois está tudo corretamente instanciado/estático.
     * @param xPos (Posição) x atual do rato
     * @param yPos (Posição) y atual do rato
     */
    public static void mousePosCallback(long window, double xPos, double yPos) {
        // Copiar os valores antigos para não os perdermos -- neste caso, coordenadas da última posição do rato
        get().lastXPos = get().xPos;
        get().lastYPos = get().yPos;

        // Receber os valores atuais -- coordenadas atuais
        get().xPos = xPos;
        get().yPos = yPos;

        // Verificar se estamos a arrastar algum elemento
        for (int indexButton = 0; indexButton < get().mouseButtons.length; indexButton++) {
            // Se estivermos a clicar em algum botão do rato, então dragging tem que ser true
            if (get().mouseButtons[indexButton]) {
                get().dragging = true;
                break;
            }
        }
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
                get().mouseButtons[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < get().mouseButtons.length) {
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
        // Guardar e dar reset a valores
        get().lastXPos = get().xPos;
        get().lastYPos = get().yPos;
        get().scrollX  = 0.0;
        get().scrollY  = 0.0;
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

    public static float getOrthoX() {
        float currentX = getX() - get().gameViewportPos.x;

        // This will convert the currentX's range, [0, 1], to [-1, 1]
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1); // 1 IS VERY IMPORTANT TO MAINTAIN THE INTEGRITY OF MATRIX MULTIPLICATION

        // See explanation of this in Camera.java file in its constructor method.
        Camera camera = Window.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);

        currentX = tmp.x;

        return currentX;
    }

    public static float getOrthoY() {
        float currentY = getY() - get().gameViewportPos.y;

        // This will convert the currentX's range, [0, 1], to [-1, 1]
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f); // Use '-' because ImGui has y coordinates flipped comparing to our project
        Vector4f tmp = new Vector4f(0, currentY, 0, 1); // 1 IS VERY IMPORTANT TO MAINTAIN THE INTEGRITY OF MATRIX MULTIPLICATION

        // See explanation of this in Camera.java file in its constructor method.
        Camera camera = Window.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);

        currentY = tmp.y;

        return currentY;
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

    public static float getDeltaX() {
        return (float) (get().lastXPos - get().xPos);
    }

    public static float getDeltaY() {
        return (float) (get().lastYPos - get().yPos);
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
        if (instance == null) {
            instance = new MouseListener();
        }

        return instance;
    }
}
