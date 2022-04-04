package jade;

import jade.editor.ICustomPropertiesWindow;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.rendering.FrameBuffer;
import jade.rendering.PickingTexture;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private ICustomPropertiesWindow iCustomPropertiesWindow;
    private int width, height;
    private final String title;
    private static Window window;
    private long glfwWindow; // Vai agir como se fosse um ponteiro
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

    private boolean runtimePlaying;

    public Window(String title, int width, int height) {
        this.title  = title;
        this.width  = 1920;
        this.height = 1080;
    }

    public Window(String title) {
        this(title, 1920, 1080);
    }

    public void init() {
        // Callback erro --se houver erros, vamos mostrá-los aqui
        GLFWErrorCallback.createPrint(System.err).set();
        /* Existem três tipos de comandos System no Java:
         * -> System.err => Output padrão dos erros do sistema (Daí usarmos este nesta situação,
         * pois "queremos" mostrar erros)
         * -> System.in => Input padrão do sistema, daí, quando criamos um Scanner para ler dados do usuário
         * fazemos:
         *         Scanner scanner = new Scanner(System.in);
         * -> System.out => Faz output de dados (não é muito certo para aqui por termos o System.err que é
         * mais específico para este caso, mas se não usaríamos o System.out).
         */

        // Inicializar o GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Error: GLFW couldn't be Initialized.");
        }

        // Configurar o GLFW (resizable window, close operation...)
        /* Configuramos primeiro e depois criamos a janela, pois o GLFW vai usar isto par criar
         * a janela
         */
        glfwDefaultWindowHints(); // Colocar tudo default primeiro

        // For linux compatibility, we have to specify the context version and make an opengl profile
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,  2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Ainda não a queremos visível
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Criar a janela
        /* O primeiro NULL serve para selecionar o monitor (NULL vai indicar o monitor primário)
         * O segundo NULL serve para ligar shared objects. Mas não é multiplataforma, por isso
         * vamos fazê-lo nós.
         */
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        // Verificar se a janela foi criada
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Error: GLFW windows couldn't be initialized");
        }

        // Configurar os callbacks do rato
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            SiriusTheFox.getWindow().setWidth(newWidth);
            SiriusTheFox.getWindow().setHeight(newHeight);
        });

        // Configurar callbacks do teclado
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Contexto do OpenGL
        glfwMakeContextCurrent(glfwWindow);

        // Ligar o v-sync (usar quantos hz o monitor tiver para dar refresh aos frames)
        glfwSwapInterval(1);

        // Agora vamos tornar a janela visível
        glfwShowWindow(glfwWindow);
    }

    public void start() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // TODO: 02/01/2022 Gets the full window size and put that here
        frameBuffer = new FrameBuffer(1920, 1080);
        pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        imGuiLayer.initImGui();
    }

    /**
     * Destroys the Window and frees the memory.
     * Also terminates the executions of the window
     */
    public void freeMemory() {
        // Free the memory
        glfwFreeCallbacks(glfwWindow); // Clean up the errors --Gives reset to the window errors
        glfwDestroyWindow(glfwWindow); // Destroys the window

        // Finish window's executions
        glfwTerminate(); // Terminates glfwWindowHint library
        glfwSetErrorCallback(null).free(); // Free the error callback
    }

    public void dispose() {
        glfwSwapBuffers(glfwWindow); /* Faz o mesmo que o Bufferstrategy, aquela parte de já termos uma
            imagem pronta para mostrar antes de apagarmos a outra. */
    }

    // TODO: 20/03/2022 Return the actually window's size

    public static int getWidth() {
        return 1920; // get().width;
    }
    public static int getHeight() {
        return 1080; // get().height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isWindowClosed() {
        return glfwWindowShouldClose(glfwWindow);
    }

    public static float getTargetAspectRatio() {
        // TODO: 02/01/2022 Get the current aspect ratio on the monitor
        return 16.0f/ 9.0f;
    }

    public FrameBuffer getFramebuffer() {
        return frameBuffer;
    }

    public ImGuiLayer getImGuiLayer() {
        return imGuiLayer;
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }

    public ICustomPropertiesWindow getICustomPropertiesWindow() {
        return iCustomPropertiesWindow;
    }

    public void setICustomPropertiesWindow(ICustomPropertiesWindow iCustomPropertiesWindow) {
        this.iCustomPropertiesWindow = iCustomPropertiesWindow;
    }
}