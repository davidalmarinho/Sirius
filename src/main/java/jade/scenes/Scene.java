package jade.scenes;

import jade.gameobjects.GameObject;
import jade.renderer.Camera;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    protected List<GameObject> gameObjectList;
    private boolean running;

    public Scene() {
        gameObjectList = new ArrayList<>();
    }

    public abstract void init();

    public void start() {
        for (GameObject g : gameObjectList) {
            g.start();
        }
        running = true;
    }

    public abstract void update(float dt);

    protected void addGameObject(GameObject gameObject) {
        gameObjectList.add(gameObject);

        // Vamos supor que spawnamos um inimigo a meio do jogo, temos que lhe dar start também
        if (running) {
            gameObject.start();
        }
    }
}
