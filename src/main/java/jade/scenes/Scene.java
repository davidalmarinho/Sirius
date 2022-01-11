package jade.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameobjects.Transform;
import imgui.ImGui;
import gameobjects.GameObject;
import gameobjects.GameObjectDeserializer;
import gameobjects.components.Component;
import gameobjects.components.ComponentDeserializer;
import jade.rendering.Camera;
import jade.rendering.Renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    protected List<GameObject> gameObjectList;
    // Game object that we are inspecting
    private boolean running;

    protected boolean levelLoaded;

    public Scene() {
        gameObjectList = new ArrayList<>();
    }

    public abstract void init();

    public abstract void update(float dt);
    public abstract void render();

    public abstract void loadResources();

    public void start() {
        for (GameObject g : gameObjectList) {
            g.start();
            this.renderer.add(g);
        }
        running = true;
    }

    public void addGameObject(GameObject gameObject) {
        gameObjectList.add(gameObject);

        // Vamos supor que spawnamos um inimigo a meio do jogo, temos que lhe dar start também
        if (running) {
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    /**
     * Gets a game object based on its id
     * @param gameObjectId game object's id
     * @return the game object that stores that id
     */
    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjectList.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();

        return result.orElse(null);
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Create custom scene integration ImGui
     */
    public void imgui() {

    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            // Save gameObjectList in a txt file
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj: gameObjectList) {
                if (obj.isDoSerialization()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String inFile = "";
        try {
            File file = new File("level.txt");
            if (file.exists()) {
                inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Means that the saving txt file isn't empty
        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObject(objs[i]);

                // Go throughout each component and check what is the greater ID
                for (Component c : objs[i].componentList) {
                    if (c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }

                // Go throughout each game object and check what is the greater ID
                if (objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }
            // Add one more to, after, set a new maximum global ID for game object and for components
            maxCompId++;
            maxGoId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);

            this.levelLoaded = true;
        }
    }
}
