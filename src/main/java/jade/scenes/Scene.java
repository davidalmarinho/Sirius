package jade.scenes;

import imgui.ImGui;
import jade.gameobjects.GameObject;
import jade.renderer.Camera;
import jade.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    protected List<GameObject> gameObjectList;
    // Game object that we are inspecting
    protected GameObject activeGameObject = null;
    private boolean running;

    public Scene() {
        gameObjectList = new ArrayList<>();
    }

    public abstract void init();

    public abstract void update(float dt);

    public abstract void loadResources();

    public void start() {
        for (GameObject g : gameObjectList) {
            g.start();
            this.renderer.add(g);
        }
        running = true;
    }

    protected void addGameObject(GameObject gameObject) {
        gameObjectList.add(gameObject);

        // Vamos supor que spawnamos um inimigo a meio do jogo, temos que lhe dar start também
        if (running) {
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void sceneImgui() {
        if (activeGameObject != null) {
            // Creates a Window
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    /**
     * Create custom scene integration ImGui
     */
    public void imgui() {

    }
}
