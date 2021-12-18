package jade;

import jade.scenes.LevelEditorScene;
import jade.scenes.LevelScene;
import jade.scenes.Scene;
import jade.scenes.Scenes;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private final String title;
    private static Window window;
    private long glfwWindow; // Vai agir como se fosse um ponteiro
    private boolean fadingToBlack = false;
    private float r, g, b, a;
    private static Scene currentScene;
    private ImGuiLayer imGuiLayer;

    private Window() {
        this.width  = 1920;
        this.height = 1080;
        this.title  = "Mario";
        this.r      = 1.0f;
        this.g      = 1.0f;
        this.b      = 1.0f;
        this.a      = 1.0f;
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

        imGuiLayer = new ImGuiLayer(glfwWindow);
        imGuiLayer.initImGui();

        // Colocar a scene
        changeScene(Scenes.LEVEL_EDITOR_SCENE);
    }

    public void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            KeyListener.updateLastKeys();

            // Carregar os eventos (teclado...)
            glfwPollEvents();

            // System.out.println("Position x: " + MouseListener.getXPos());
            // System.out.println("Position y " + MouseListener.getYPos());
            // if (KeyListener.isKeyUp(GLFW_KEY_SPACE)) {
            //     System.out.println("Hello World!");
            // }

            // Limpar a frame com uma cor
            glClearColor(r, g, b, a); /* Especifica a cor que o glClear vai usar para
            limpar a color buffers */

            if (KeyListener.isKeyDown(GLFW_KEY_SPACE)) fadingToBlack = true;

            if (fadingToBlack) {
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
            }

            glClear(GL_COLOR_BUFFER_BIT); /* Contar para o OpenGL como limpar a frame (Indicates the buffers
            currently enabled for color writing).*/

            if (dt >= 0) {
                // System.out.println("FPS: " + 1.0f / dt);
                currentScene.update(dt);
            }

            imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow); /* Faz o mesmo que o Bufferstrategy, aquela parte de já termos uma
            imagem pronta para mostrar antes de apagarmos a outra. */

            // Gameloop
            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void changeScene(Scenes scene) {
        switch (scene) {
            case LEVEL_EDITOR_SCENE:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case LEVEL_SCENE:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unknown scene '" + scene + "'";
                break;
        }
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

    public static Scene getCurrentScene() {
        return currentScene;
    }
}