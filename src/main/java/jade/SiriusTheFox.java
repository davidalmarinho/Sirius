package jade;

import audio.Audio;
import gameobjects.GameObject;
import gameobjects.ICustomPrefabs;
import jade.editor.ICustomPropertiesWindow;
import jade.input.Input;
import jade.input.MouseListener;
import jade.rendering.Color;
import jade.rendering.Renderer;
import jade.rendering.Shader;
import jade.rendering.debug.DebugDraw;
import jade.scenes.LevelEditorSceneInitializer;
import jade.scenes.LevelSceneInitializer;
import jade.scenes.Scene;
import jade.scenes.ISceneInitializer;
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

    public static boolean exportGame = false;

    private ISceneInitializer customSceneInitializer;

    private SiriusTheFox() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        window = new Window("Mario", 1920, 1080);
        audio = new Audio();

        EventSystem.addObserver(this);
    }

    public void init() {
        window.init();
        audio.init();
        window.start();

        // Colocar a scene
        if (!exportGame)
            changeScene(new LevelEditorSceneInitializer());
        else {
            this.runtimePlaying = true;
            if (customSceneInitializer != null)
                changeScene(customSceneInitializer.build());
            else
                changeScene(new LevelSceneInitializer());
        }
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
            glClearColor(0f, 0f, 0f, 0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            Renderer.bindShader(pickingShader);

            currentScene.render();

            window.getPickingTexture().disableWriting();
            glEnable(GL_BLEND);

            // ========================================
            // Render pass 2. render actual game
            // ========================================
            if (!exportGame)
                DebugDraw.beginFrame();

            if (!exportGame)
                window.getFramebuffer().bind();

            // Cleanup the frame with a color
            Color color = currentScene.getCamera().clearColor;
            glClearColor(color.getColor().x, color.getColor().y, color.getColor().z, color.getColor().w); /* Specifies
            the color that glClear will use to cleaup buffer's color.*/

            glClear(GL_COLOR_BUFFER_BIT); /* Tell OpenGL to cleanup the frame (Indicates the buffers
            currently enabled for color writing).*/

            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                // System.out.println("FPS: " + 1.0f / dt);
                if (runtimePlaying)
                    currentScene.update(dt);
                else
                    currentScene.editorUpdate(dt);

                currentScene.render();

                if (!exportGame)
                    DebugDraw.draw();
            }

            if (!exportGame) {
                window.getFramebuffer().unbind();
                window.getImGuiLayer().update(dt, currentScene);
            }
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

    public static void changeScene(ISceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }
        getWindow().getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
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
                if (customSceneInitializer != null)
                    changeScene(customSceneInitializer.build());
                else
                    changeScene(new LevelSceneInitializer());
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
            case EXPORT_GAME:
                SiriusTheFox.exportGame = true;
                this.runtimePlaying = true;
                if (customSceneInitializer != null)
                    changeScene(customSceneInitializer.build());
                else
                    changeScene(new LevelSceneInitializer());
                break;
        }
    }

    public void addCustomizedPropertiesWindow(ICustomPropertiesWindow iCustomPropertiesWindow) {
        window.setICustomPropertiesWindow(iCustomPropertiesWindow);
    }

    public void addCustomLevelSceneInitializer(ISceneInitializer customSceneInitializer) {
        this.customSceneInitializer = customSceneInitializer;
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

    public static ISceneInitializer getCustomSceneInitializer() {
        return get().customSceneInitializer;
    }

    public static SiriusTheFox get() {
        if (instance == null) instance = new SiriusTheFox();

        return instance;
    }

    public boolean isRuntimePlaying() {
        return runtimePlaying;
    }
}
