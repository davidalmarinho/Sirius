package sirius.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameobjects.components.Transform;
import gameobjects.GameObject;
import gameobjects.GameObjectDeserializer;
import gameobjects.components.Component;
import gameobjects.components.ComponentDeserializer;
import observers.EventSystem;
import observers.events.Event;
import observers.events.Events;
import sirius.SiriusTheFox;
import sirius.editor.MouseControls;
import sirius.editor.NonPickable;
import sirius.editor.PropertiesWindow;
import sirius.input.KeyListener;
import sirius.levels.Level;
import sirius.rendering.Camera;
import sirius.rendering.Renderer;
import org.joml.Vector2f;
import physics2d.Physics2d;
import sirius.utils.AssetPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Scene {
    private Renderer renderer;
    private Camera camera;
    private Physics2d physics2d;
    private List<GameObject> gameObjectList;
    private List<GameObject> pendingGameObjectList;
    // Game object that we are inspecting
    private boolean running;

    private ISceneInitializer sceneInitializer;

    public Scene(ISceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2d = new Physics2d();
        this.renderer = new Renderer();
        gameObjectList = new ArrayList<>();
        pendingGameObjectList = new ArrayList<>();
        running = false;

        loadLevels();
    }

    public void init() {
        this.camera = new Camera(new Vector2f(0, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void editorUpdate(float dt) {
        /*time -= dt;

        if (time <= 0) {
            for (GameObject gameObject : SiriusTheFox.getCurrentScene().getGameObjectList()) {
                System.out.println("IDS: " + gameObject.getUid());
            }
            time = 1.0f;
        }*/

        // Save and load file
        if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_S)
                && !SiriusTheFox.get().isRuntimePlaying()) {
            save();
        } else if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_O))
            load();
        else if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_E))
            EventSystem.notify(null, new Event(Events.EXPORT_GAME));

        this.camera.adjustProjection();

        if (KeyListener.isKeyDown(GLFW_KEY_T)) {
            for (GameObject go : gameObjectList) {
                System.out.println("ID: " + go.getUid());
            }
        }

        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject go = gameObjectList.get(i);
            go.editorUpdate(dt);

            if (go.isDead()) {
                gameObjectList.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2d.destroyGameObject(go);

                // To prevent from skipping another game objects
                i--;
            }
        }

        addSceneAtRuntime();
    }

    public void update(float dt) {
        this.camera.adjustProjection();
        this.physics2d.update(dt);

        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject go = gameObjectList.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjectList.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2d.destroyGameObject(go);

                // To prevent from skipping another game objects
                i--;
            }
        }

        addSceneAtRuntime();
    }

    // Let's suppose that we spawn an enemy while the game is running, we have to add it too
    private void addSceneAtRuntime() {
        for (int i = 0; i < pendingGameObjectList.size(); i++) {
            GameObject go = pendingGameObjectList.get(i);
            gameObjectList.add(go);
            go.start();
            this.renderer.add(go);
            this.physics2d.add(go);
        }
        pendingGameObjectList.clear();
    }

    public void render() {
        this.renderer.render();
    }

    public void start() {
        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject g = gameObjectList.get(i);
            g.start();
            this.renderer.add(g);
            this.physics2d.add(g);
        }
        running = true;
    }

    /**
     * Adds a game object to a queue list if the game isn't been run. Or adds the game object directly to the game.
     *
     * @param gameObject The game object that will be added to the pendingGameObjectList or to the  gameObjectList.
     */
    public void addGameObject(GameObject gameObject) {
        if (running)
            pendingGameObjectList.add(gameObject);
        else
            gameObjectList.add(gameObject);
    }

    /**
     * Gets a game object based on its id.
     *
     * @param gameObjectId game object's id
     * @return the game object that stores that id
     */
    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjectList.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();

        return result.orElse(null);
    }

    /**
     * Gets a game object based on its name.
     *
     * @param gameObjectName game object's name
     * @return the game object that stores that name
     */
    public GameObject getGameObject(String gameObjectName) {
        Optional<GameObject> result = this.gameObjectList.stream()
                .filter(gameObject -> gameObject.name.equals(gameObjectName))
                .findFirst();

        return result.orElse(null);
    }

    /**
     * Gets a game object based on its components.
     *
     * @param componentClass Component that we are looking to.
     * @return the game object with the pretended component.
     */
    public <T extends Component> GameObject getGameObjectWith(Class<T> componentClass) {
        return gameObjectList.stream().filter(gameObject -> gameObject.hasComponent(componentClass))
                .findFirst().orElse(null);
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Create custom scene integration ImGui
     */
    public void imgui() {
        this.sceneInitializer.imgui();
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        return go;
    }

    public void destroy() {
        for (GameObject go : gameObjectList) {
            go.destroy();
        }
    }

    public void loadLevels() {
        File folder = new File("assets/levels");
        File[] listOfLevels = folder.listFiles();
        assert listOfLevels != null;

        // Sort level's list according its size --secondarily the names are also alphabetical ordered
        Arrays.sort(listOfLevels, Comparator.comparingInt(o -> o.getName().length()));

        // Array to store what levels were loaded
        int[] loadedLevels = new int[listOfLevels.length];

        // Load levels to the asset pool
        for (int i = 0; i < listOfLevels.length; i++) {
            // Check if the saved file is valid
            if (!listOfLevels[i].getName().contains("level") || !listOfLevels[i].getName().contains(".json")) {
                System.err.println("Warning: '" + listOfLevels[i].getName()
                        + "' can't be recognized as a level file.");
                continue;
            }

            String[] navigator = listOfLevels[i].getName().split("level");
            String[] navigator1 = navigator[1].split(".json");
            int curLvl = Integer.parseInt(navigator1[0]);
            AssetPool.addLevel(new Level(listOfLevels[i].getName(), listOfLevels[i].getPath(), curLvl));
            loadedLevels[i] = curLvl;
        }

        Arrays.sort(loadedLevels);

        // Throw a warning if there are missing levels
        int expectedLevel = 1;
        for (int i = 0; i < loadedLevels.length; i++) {
            while (expectedLevel != loadedLevels[i]) {
                System.err.println("Warning: Level " + expectedLevel + " doesn't exist!");
                expectedLevel++;
            }

            expectedLevel++;
        }

        Level.maxLevel = loadedLevels[loadedLevels.length - 1];
    }

    public void save() {
        // ===============================================================================
        // Bug fix:
        // Modifies the selected active game objects if they haven't been modified before
        PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();

        if (sceneInitializer instanceof LevelEditorSceneInitializer) {
            LevelEditorSceneInitializer levelEditor = (LevelEditorSceneInitializer) sceneInitializer;
            MouseControls mouseControls = levelEditor.getLevelEditorStuff().getComponent(MouseControls.class);

            if (propertiesWindow.getActiveGameObjectList().size() > 1) {
                List<GameObject> activeGameObjectList = new ArrayList<>(propertiesWindow.getActiveGameObjectList());
                mouseControls.changeAllGameObjects(activeGameObjectList.get(0), activeGameObjectList);
                propertiesWindow.clearSelected();
            }

            gameObjectList.addAll(pendingGameObjectList);
            pendingGameObjectList.clear();
        }
        // ===============================================================================

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        try {
            // Save gameObjectList in a txt file
            FileWriter writer = new FileWriter(AssetPool.getLevel(Level.currentLevel).getPath());
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : gameObjectList) {

                // Bug fix --Don't save game objects that are attached to the cursor
                if (obj.hasComponent(NonPickable.class)) continue;

                if (obj.isDoSerialization()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            // writer.flush();
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
                .enableComplexMapKeySerialization()
                .create();
        String inFile = "";
        try {
            Level level = AssetPool.getLevel(Level.currentLevel);
            File file = new File(level.getPath());
            if (file.exists()) {
                inFile = new String(Files.readAllBytes(Paths.get(file.getPath())));
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
        }
    }

    public List<GameObject> getGameObjectList() {
        return gameObjectList;
    }

    public Physics2d getPhysics() {
        return this.physics2d;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public ISceneInitializer getSceneInitializer() {
        return sceneInitializer;
    }

    public boolean isRunning() {
        return running;
    }
}
