package sirius.scenes;

import gameobjects.components.Transform;
import gameobjects.GameObject;
import gameobjects.components.Component;
import observers.EventSystem;
import observers.events.Event;
import observers.events.Events;
import sirius.editor.imgui.sprite_animation_window.AnimationBlueprint;
import sirius.encode_tools.Encode;
import sirius.SiriusTheFox;
import sirius.editor.MouseControls;
import sirius.editor.PropertiesWindow;
import sirius.input.KeyListener;
import sirius.levels.Level;
import sirius.rendering.Camera;
import sirius.rendering.Renderer;
import org.joml.Vector2f;
import physics2d.Physics2d;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

import java.io.File;
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
        loadAnimations();
        this.sceneInitializer.init(this);
    }

    public void editorUpdate(float dt) {
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

    public void renderUserInterface() {
        this.renderer.renderUserInterface();
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

    private void loadLevels() {
        File folder = new File(Settings.Files.LEVELS_FOLDER);
        File[] levels = folder.listFiles();
        assert levels != null;

        // Array to store what levels were loaded
        int[] loadedLevels = new int[levels.length];

        // Load levels to the asset pool
        for (int i = 0; i < levels.length; i++) {
            // Check if the saved file is valid
            if (!levels[i].getName().contains("level") || !levels[i].getName().contains(".json")) {
                System.err.println("Warning: '" + levels[i].getName()
                        + "' can't be recognized as a level file.");
                continue;
            }

            String[] navigator = levels[i].getName().split("level");
            String[] navigator1 = navigator[1].split(".json");
            int curLvl = Integer.parseInt(navigator1[0]);
            loadedLevels[i] = curLvl;
        }

        Arrays.sort(loadedLevels);

        for (int i = 0; i < loadedLevels.length; i++) {
            int curLvl = loadedLevels[i];
            for (int j = 0; j < levels.length; j++) {
                String lvlName = levels[j].getName();
                String[] navigator = lvlName.split("level");
                String[] navigator1 = navigator[1].split(".json");
                int levelFile = Integer.parseInt(navigator1[0]);

                if (curLvl == levelFile) {
                    AssetPool.addLevel(new Level(lvlName, levels[j].getPath(), curLvl));
                    break;
                }
            }
        }

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

    private void loadAnimations() {
        File directory = new File(Settings.Files.ANIMATIONS_FOLDER);
        File[] animations = directory.listFiles();

        for (int i = 0; i < Objects.requireNonNull(animations).length; i++) {
            File file = animations[i];
            AnimationBlueprint animationBlueprint = Encode.getAnimation(file.getPath());
            AssetPool.addAnimation(file.getPath(), animationBlueprint);
        }
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

        // Save game objects
        Encode.saveGameObjectListInFile(gameObjectList);
    }

    public void load() {
        Level level = AssetPool.getLevel(Level.currentLevel);
        String inFile = Encode.readFile(level.getPath());

        // Means that the saving txt file isn't empty
        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = Encode.getGameObjectsFromFile(level.getPath());
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
