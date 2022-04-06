package jade;

import audio.Audio;
import gameobjects.GameObject;
import gameobjects.ICustomPrefabs;
import jade.editor.ICustomPropertiesWindow;
import jade.input.Input;
import jade.input.MouseListener;
import jade.rendering.Renderer;
import jade.rendering.Shader;
import jade.rendering.debug.DebugDraw;
import jade.scenes.LevelEditorSceneInitializer;
import jade.scenes.Scene;
import jade.scenes.SceneInitializer;
import jade.utils.AssetPool;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.lwjgl.Version;
import physics2d.Physics2d;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class SiriusTheFox implements Observer {
    private static SiriusTheFox instance;
    private Window window;
    private Audio audio;
    private static Scene currentScene;

    private boolean runtimePlaying;

    private SiriusTheFox() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        window = new Window("Mario", 1920, 1080);
        audio  = new Audio();

        EventSystem.addObserver(this);
    }

    public void init() {
        window.init();
        audio.init();
        window.start();

        // Colocar a scene
        changeScene(new LevelEditorSceneInitializer());
    }

    public void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!window.isWindowClosed()) {
            Input.updateEvents();

            // ========================================
            // Render pass 1. render to picking texture
            // ========================================
            glDisable(GL_BLEND);
            window.getPickingTexture().enableWriting();
            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            Renderer.bindShader(pickingShader);

            currentScene.render();

            window.getPickingTexture().disableWriting();
            glEnable(GL_BLEND);

            // ========================================
            // Render pass 2. render actual game
            // ========================================
            Renderer.bindShader(defaultShader);

            DebugDraw.beginFrame();

            window.getFramebuffer().bind();

            // Limpar a frame com uma cor
            glClearColor(1f, 1f, 1f, 1f); /* Especifica a cor que o glClear vai usar para
            limpar a color buffers */

            glClear(GL_COLOR_BUFFER_BIT); /* Contar para o OpenGL como limpar a frame (Indicates the buffers
            currently enabled for color writing).*/

            if (dt >= 0) {
                // System.out.println("FPS: " + 1.0f / dt);
                if (runtimePlaying)
                    currentScene.update(dt);
                else
                    currentScene.editorUpdate(dt);

                currentScene.render();
                DebugDraw.draw();
            }

            window.getFramebuffer().unbind();

            window.getImGuiLayer().update(dt, currentScene);
            MouseListener.endFrame();
            window.dispose();

            // Gameloop
            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public void run() {
        init();
        loop();

        /* This part isn't strictly necessary because the operative system usually cleans up the memory for us. But
         * just for safe, we will clean it by ourselves and because this is good practice.
         */

        audio.freeMemory();
        window.freeMemory();
    }

    private void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }
        window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    @Override
    public void onNotify(GameObject gameObject, Event event) {
        switch (event.type) {
            case GAME_ENGINE_START_PLAY:
                currentScene.save();
                this.runtimePlaying = true;
                changeScene(new LevelEditorSceneInitializer());
                break;
            case GAME_ENGINE_STOP_PLAY:
                this.runtimePlaying = false;
                changeScene(new LevelEditorSceneInitializer());
                break;
            case LOAD_LEVEL:
                changeScene(new LevelEditorSceneInitializer());
                break;
            case SAVE_LEVEL:
                currentScene.save();
                break;
        }
    }

    public void addCustomizedPropertiesWindow(ICustomPropertiesWindow iCustomPropertiesWindow) {
        window.setICustomPropertiesWindow(iCustomPropertiesWindow);
    }

    public void addRuntimeOptionCustomizedPrefabs(ICustomPrefabs iCustomPrefabs) {
        window.setICustomPrefabs(iCustomPrefabs);
    }

    public static Window getWindow() {
        return get().window;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static Physics2d getPhysics() {
        return getCurrentScene().getPhysics();
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().window.getImGuiLayer();
    }

    public static SiriusTheFox get() {
        if (instance == null) instance = new SiriusTheFox();

        return instance;
    }
}
