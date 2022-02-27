package gameobjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameobjects.components.Component;
import gameobjects.components.ComponentDeserializer;
import gameobjects.components.SpriteRenderer;
import gameobjects.components.Transform;
import imgui.ImGui;
import jade.utils.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;

    public final String NAME;
    public List<Component> componentList;
    public transient Transform transform;
    private boolean dead;
    private boolean doSerialization = true;

    public GameObject(String name) {
        this.NAME = name;
        componentList = new ArrayList<>();

        this.uid = ID_COUNTER++;
    }

    public void start() {
        for (int i = 0; i < componentList.size(); i++) {
            componentList.get(i).start();
        }
    }

    public void editorUpdate(float dt) {
        for (Component c : componentList) {
            c.editorUpdate(dt);
        }
    }

    public void update(float dt) {
        for (Component c : componentList) {
            c.update(dt);
        }
    }

    /**
     * Gets the game object desirable component if it exists.
     *
     * @param ComponentClass The class of the component that you are looking for.
     * @return The desirable component if it exists.
     *
     * Example: gameObject.getComponent(SpriteRenderer.class);
     */
    public <T extends Component> T getComponent(Class<T> ComponentClass) {
        for (Component component : componentList) {
            if (ComponentClass.isAssignableFrom(component.getClass())) {
                try {
                    return ComponentClass.cast(component);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Couldn't cast component";
                }
            }
        }

        return null;
    }

    /**
     * Checks if a game object has the component you are looking for.
     *
     * @param ComponentClass The class of the component that you are looking for.
     * @return true if the game object has the component.
     *
     * Example: boolean haveComponent = gameObject.hasComponent(SpriteRenderer.class)
     */
    public <T extends Component> boolean hasComponent(Class<T> ComponentClass) {
        return componentList.stream().anyMatch(component -> ComponentClass.isAssignableFrom(component.getClass()));
    }

    /**
     * Removes a component from the game object.
     *
     * @param ComponentClass The class of the component that you want to remove.
     *
     * Example: gameObject.removeComponent(RigidBody2d.class);
     */
    public <T extends Component> void removeComponent(Class<T> ComponentClass) {
        for (int i = 0; i < componentList.size(); i++) {
            Component component = componentList.get(i);

            if (ComponentClass.isAssignableFrom(component.getClass())) {
                componentList.remove(ComponentClass.cast(component));
            }
        }
    }

    public void destroy() {
        dead = true;
        for (int i = 0; i < componentList.size(); i++) {
            componentList.get(i).destroy();
        }
    }

    public GameObject copy() {
        // TODO: 27/02/2022 Make this better please
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);
        obj.generateUid();

        for (Component c : obj.componentList) {
            c.generateId();
        }

        SpriteRenderer spriteRenderer = obj.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null && spriteRenderer.getTexture() != null) {
            spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilePath()));
        }
        return obj;
    }

    /**
     * Adds a component to game object.
     *
     * @param component Desirable component.
     */
    public void addComponent(Component component) {
        // Set an ID
        component.generateId();

        componentList.add(component);
        component.gameObject = this;
    }

    public void imgui() {
        for (Component c : componentList) {
            // When a bar is opened, we parse to ImGui the fields of that class
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public int getUid() {
        return uid;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean isDoSerialization() {
        return this.doSerialization;
    }

    public boolean isDead() {
        return dead;
    }
}
