package sirius;

import gameobjects.ICustomPrefabs;
import sirius.editor.ICustomPropertiesWindow;
import sirius.input.KeyListener;
import sirius.input.MouseListener;
import sirius.rendering.FrameBuffer;
import sirius.rendering.PickingTexture;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    // TODO: 05/04/2022 Should go to imGuiLayer
    // Custom modifications
    private ICustomPropertiesWindow iCustomPropertiesWindow;
    private ICustomPrefabs iCustomPrefabs;

    private int width, height;
    private int maxWidth, maxHeight;
    private final String title;
    private long glfwWindow; // Will act as a pointer
    private long monitor;
    private boolean fullscreen = true;
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

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
        monitor = glfwGetPrimaryMonitor();

        // TODO: 13/09/2021 Define WIDTH and HEIGHT
        glfwWindow = glfwCreateWindow(1920, 1080, this.title, NULL, NULL);

        // Verificar se a janela foi criada
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Error: GLFW windows couldn't be initialized");
        }

        // Configurar os callbacks do rato
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            setWidth(newWidth);
            setHeight(newHeight);
        });

        // Configurar callbacks do teclado
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Center window
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            assert vidMode != null;

            // Register the max size the window can take
            maxWidth = vidMode.width();
            maxHeight = vidMode.height();

            glfwSetWindowPos(glfwWindow,
                    (vidMode.width() / 2 - pWidth.get(0) / 2),
                    (vidMode.height() / 2 - pHeight.get(0) / 2));

            if (fullscreen)
                glfwSetWindowMonitor(glfwWindow, monitor, 0, 0, vidMode.width(), vidMode.height(), 0);
        }


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
        frameBuffer = new FrameBuffer(getWidth(), getHeight());
        pickingTexture = new PickingTexture(getWidth(), getHeight());

        glViewport(0, 0, getWidth(), getHeight());

        imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        imGuiLayer.initImGui();

        glfwFocusWindow(glfwWindow);
    }

    public void update() {
        if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_F)) {
            fullscreen = !fullscreen;
            GLFWVidMode glfwVidMode = glfwGetVideoMode(monitor);

            if (fullscreen)
                glfwSetWindowMonitor(glfwWindow, monitor, 0, 0, glfwVidMode.width(), glfwVidMode.height(), 0);
            else
                glfwSetWindowMonitor(glfwWindow, NULL, 0, 0, 1920, 1080, 0);
        }
    }

    public void destroy() {
        imGuiLayer.destroyImGui();
        glfwDestroyWindow(glfwWindow);
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

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public boolean isWindowClosed() {
        return glfwWindowShouldClose(glfwWindow);
    }

    public float getTargetAspectRatio() {
        return (float) getWidth() / getHeight();
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

    public ICustomPrefabs getICustomPrefabs() {
        return iCustomPrefabs;
    }

    public void setICustomPrefabs(ICustomPrefabs iCustomPrefabs) {
        this.iCustomPrefabs = iCustomPrefabs;
    }
}