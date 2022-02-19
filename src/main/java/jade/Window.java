package jade;

import gameobjects.GameObject;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.rendering.FrameBuffer;
import jade.rendering.PickingTexture;
import jade.rendering.Renderer;
import jade.rendering.Shader;
import jade.rendering.debug.DebugDraw;
import jade.scenes.LevelEditorScene;
import jade.scenes.LevelScene;
import jade.scenes.Scene;
import jade.scenes.Scenes;
import jade.utils.AssetPool;
import observers.EventSystem;
import observers.Observer;
import observers.events.EEventType;
import observers.events.Event;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private int width, height;
    private final String title;
    private static Window window;
    private long glfwWindow; // Vai agir como se fosse um ponteiro
    private static Scene currentScene;
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

    private Window() {
        this.width  = 1920;
        this.height = 1080;
        this.title  = "Mario";
        EventSystem.addObserver(this);
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        /* Esta parte não é estritamente necessária, porque o, sistema operativo,
         * já costuma limpar a memória, mas podemos ser nós a limpar o nosso lixo :)
         */

        // Libertar a memória
        glfwFreeCallbacks(glfwWindow); // Limpar os erros --Dá reset aos erros da janela
        glfwDestroyWindow(glfwWindow); // Destruir a janela

        // Terminar todas as execuções da janela
        glfwTerminate(); // Termina a biblioteca glfwWindowHint
        glfwSetErrorCallback(null).free(); // Libertar o error callback
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
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Configurar callbacks do teclado
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Contexto do OpenGL
        glfwMakeContextCurrent(glfwWindow);

        // Ligar o v-sync (usar quantos hz o monitor tiver para dar refresh aos frames)
        glfwSwapInterval(1);

        // Agora vamos tornar a janela visível
        glfwShowWindow(glfwWindow);

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

        // Colocar a scene
        changeScene(Scenes.LEVEL_EDITOR_SCENE);
    }

    public void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            KeyListener.updateLastKeys();

            // Carregar os eventos (teclado...)
            glfwPollEvents();

            // ========================================
            // Render pass 1. render to picking texture
            // ========================================
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();
            glViewport(0, 0, 1920, 1080);
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            Renderer.bindShader(pickingShader);

            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // ========================================
            // Render pass 2. render actual game
            // ========================================
            Renderer.bindShader(defaultShader);

            DebugDraw.beginFrame();

            frameBuffer.bind();

            // Limpar a frame com uma cor
            glClearColor(1f, 1f, 1f, 1f); /* Especifica a cor que o glClear vai usar para
            limpar a color buffers */

            glClear(GL_COLOR_BUFFER_BIT); /* Contar para o OpenGL como limpar a frame (Indicates the buffers
            currently enabled for color writing).*/

            if (dt >= 0) {
                DebugDraw.draw();
                // System.out.println("FPS: " + 1.0f / dt);
                currentScene.update(dt);
                currentScene.render();
            }

            frameBuffer.unbind();

            imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow); /* Faz o mesmo que o Bufferstrategy, aquela parte de já termos uma
            imagem pronta para mostrar antes de apagarmos a outra. */

            MouseListener.endFrame();

            // Gameloop
            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
    }

    private void changeScene(Scenes scene) {
        switch (scene) {
            case LEVEL_EDITOR_SCENE:
                currentScene = new LevelEditorScene();
                break;
            case LEVEL_SCENE:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + scene + "'";
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }

        return window;
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int width) {
        get().width = width;
    }

    public static void setHeight(int height) {
        get().height = height;
    }

    public static FrameBuffer getFramebuffer() {
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio() {
        // TODO: 02/01/2022 Get the current aspect ratio on the monitor
        return 16.0f/ 9.0f;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public ImGuiLayer getImGuiLayer() {
        return imGuiLayer;
    }

    @Override
    public void onNotify(GameObject gameObject, Event event) {
        if (event.type == EEventType.GAME_ENGINE_START_PLAY) {
            System.out.println("Starting ");
        } else if (event.type == EEventType.GAME_ENGINE_STOP_PLAY) {
            System.out.println("Stopping...");
        }
    }
}