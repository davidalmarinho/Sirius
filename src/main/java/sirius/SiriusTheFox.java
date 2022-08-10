package sirius;

import audio.Audio;
import gameobjects.GameObject;
import observers.events.Events;
import sirius.editor.imgui.ICustomPrefabs;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.editor.imgui.ImGuiLayer;
import sirius.encode_tools.Encode;
import sirius.input.Input;
import sirius.input.KeyListener;
import sirius.input.MouseListener;
import sirius.rendering.color.Color;
import sirius.rendering.Renderer;
import sirius.rendering.Shader;
import sirius.rendering.debug.DebugDraw;
import sirius.scenes.LevelEditorSceneInitializer;
import sirius.scenes.LevelSceneInitializer;
import sirius.scenes.Scene;
import sirius.scenes.ISceneInitializer;
import sirius.utils.AssetPool;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.lwjgl.Version;
import physics2d.Physics2d;
import sirius.utils.Settings;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

public class SiriusTheFox implements Observer {
    private static SiriusTheFox instance;
    private Window window;
    private Audio audio;
    private static Scene currentScene;

    private boolean runtimePlaying;

    private boolean maximizeOnPlay = false;

    private ISceneInitializer customSceneInitializer;

    boolean flag = true;

    private SiriusTheFox() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        window = new Window("Sirius, the Fox!", 1920, 1080);
        audio = new Audio();

        EventSystem.addObserver(this);
    }

    public void init() {
        window.init();
        audio.init();
        window.start();

        // Put the scene
        // if (!maximizeOnPlay)
        loadEngineResources();
        changeScene(new LevelEditorSceneInitializer());
        // else {
        //     this.runtimePlaying = true;
        //     if (customSceneInitializer != null)
        //         changeScene(customSceneInitializer.build());
        //     else
        //         changeScene(new LevelSceneInitializer());
        // }
    }

    public void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader fontShader    = AssetPool.getShader("assets/shaders/font.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!window.isWindowClosed()) {
            if ((dt >= 0 && window.isVsync()) || (dt >= window.getUpdateFps() && !window.isVsync())) {
                // ========================================
                // Render pass 1. render to picking texture
                // ========================================
                glDisable(GL_BLEND);
                window.getPickingTexture().enableWriting();

                // if (!export) {
                //     glViewport(0, 0,
                //             (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                //             (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                // } else {
                //     glViewport(0, 0, window.getWidth(), window.getHeight());
                // }
                if (!maximizeOnPlay)
                    glViewport(0, 0,
                            (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                else
                    glViewport(0, 0, window.getWidth(), window.getHeight());

                glClearColor(0f, 0f, 0f, 0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                Renderer.bindShader(pickingShader);

                currentScene.render();

                window.getPickingTexture().disableWriting();
                glEnable(GL_BLEND);

                // ========================================
                // Render pass 2. render actual game
                // ========================================

                // if (!export)
                //     DebugDraw.beginFrame();
                DebugDraw.beginFrame();

                // if (!export)
                //     window.getFramebuffer().bind();
                if (!maximizeOnPlay) {
                    window.getFramebuffer().bind();
                } else {
                    if (!runtimePlaying) {
                        window.getFramebuffer().bind();
                    }
                }

                // Cleanup the frame with a color
                Color color = currentScene.getCamera().clearColor;

                glClearColor(color.getColor().x, color.getColor().y, color.getColor().z, color.getColor().w); /* Specifies
                the color that glClear will use to clean up buffer's color.*/

                glClear(GL_COLOR_BUFFER_BIT); /* Tell OpenGL to clea nup the frame (Indicates the buffers
                currently enabled for color writing).*/

                Input.updateEvents();
                // System.out.println("FPS: " + 1.0f / dt);
                Renderer.bindShader(defaultShader);
                window.update();

                if (runtimePlaying)
                    currentScene.update(dt);
                else
                    currentScene.editorUpdate(dt);

                currentScene.render();
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

                Renderer.bindShader(fontShader);
                currentScene.renderFontInGame();

                // if (!export) {
                    // DebugDraw.draw();
                // }
                if (!runtimePlaying)
                    DebugDraw.draw();


                // if (!export) {
                //     window.getFramebuffer().unbind();
                //     window.getImGuiLayer().update(dt, currentScene);
                // }
                if (!maximizeOnPlay) {
                    window.getFramebuffer().unbind();
                    window.getImGuiLayer().update(dt, currentScene);
                } else {
                    if (!runtimePlaying) {
                        window.getFramebuffer().unbind();
                        window.getImGuiLayer().update(dt, currentScene);
                    }
                }

                // TODO: 04/08/2022 Put this somewhere else
                if (KeyListener.isKeyDown(GLFW_KEY_ESCAPE) && maximizeOnPlay && runtimePlaying) {
                    // if (!export) {
                    //     onNotify(null, new Event(Events.GAME_ENGINE_STOP_PLAY));
                    //     getImGuiLayer().getGameViewWindow().setPlaying(false);
                    // }
                    onNotify(null, new Event(Events.GAME_ENGINE_STOP_PLAY));
                    getImGuiLayer().getGameViewWindow().setPlaying(false);
                }

                if (!window.isVsync()) {
                    dt = 0.0f;
                }
                MouseListener.endFrame();
                window.dispose();
            }

            endTime = (float) glfwGetTime();
            if (window.isVsync()) {
                dt = endTime - beginTime;
            } else {
                dt += endTime - beginTime;
            }
            beginTime = endTime;
        }

        // Save docks visibility --the docks' visibility are loaded in ImGuiLayer class
        String[] options = new String[]{
                "showGameViewWindow", "showSpriteAnimationWindow", "showTabBar",
                "showToolWindow", "showSceneHierarchy"};
        Boolean[] values = new Boolean[]{
                getImGuiLayer().getGameViewWindow().isVisible(),
                getImGuiLayer().getSpriteAnimationWindow().isVisible(),
                getImGuiLayer().getTabBar().isVisible(),
                getImGuiLayer().getToolWindow().isVisible(),
                getImGuiLayer().getSceneHierarchy().isVisible()};
        Encode.saveInFile(options, values, Settings.Files.GUI_VISIBILITY_SETTINGS, 0);
    }

    private void updateEngine() {

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

    public static void loadEngineResources() {
        AssetPool.addAllShaders();
        AssetPool.addAllFonts();
    }

    @Override
    public void onNotify(GameObject gameObject, Event event) {
        switch (event.type) {
            case GAME_ENGINE_START_PLAY -> {
                currentScene.save();
                this.runtimePlaying = true;
                if (customSceneInitializer != null)
                    changeScene(customSceneInitializer.build());
                else
                    changeScene(new LevelSceneInitializer());
            }
            case GAME_ENGINE_STOP_PLAY -> {
                this.runtimePlaying = false;
                changeScene(new LevelEditorSceneInitializer());
            }
            case LOAD_LEVEL -> {
                currentScene.load();
                changeScene(new LevelEditorSceneInitializer());
            }
            case SAVE_LEVEL -> currentScene.save();

            // TODO: 04/08/2022 Unbind gui libs when exporting game
            case EXPORT_GAME -> {
                // this.maximizeOnPlay = true;
                // this.runtimePlaying = true;
                // if (customSceneInitializer != null)
                //     changeScene(customSceneInitializer.build());
                // else
                //     changeScene(new LevelSceneInitializer());
            }
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

    // public void setReadyToExport(boolean readyToExport) {
    //     this.maximizeOnPlay = true;
    // }

    public static Window getWindow() {
        return get().window;
    }

    public static void setWindow(Window window) {
        SiriusTheFox.get().window = window;
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

    public boolean isRuntimePlaying() {
        return runtimePlaying;
    }

    public boolean isMaximizeOnPlay() {
        return maximizeOnPlay;
    }

    public void setMaximizeOnPlay(boolean maximizeOnPlay) {
        this.maximizeOnPlay = maximizeOnPlay;
    }

    public static SiriusTheFox get() {
        if (instance == null) instance = new SiriusTheFox();

        return instance;
    }
}
