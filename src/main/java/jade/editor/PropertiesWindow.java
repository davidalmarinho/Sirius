package jade.editor;

import gameobjects.GameObject;
import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import gameobjects.components.game_components.Ground;
import imgui.ImGui;
import jade.SiriusTheFox;
import jade.rendering.PickingTexture;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2d;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private static PropertiesWindow instance;

    private List<GameObject> activeGameObjectList;
    private GameObject activeGameObject = null;
    // private boolean registryLastPosition;
    // private Vector2f activeGameObjectLastPosition;
    private List<Vector4f> activeGameObjectOriginalColorList;
    private PickingTexture pickingTexture;

    private PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjectList = new ArrayList<>();
        // this.activeGameObjectLastPosition = new Vector2f();
        // this.registryLastPosition = true;
        this.activeGameObjectOriginalColorList = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {
        if ((activeGameObjectList.size() > 0 && activeGameObjectList.get(0) != null)
                && MouseControls.allComponentsHaveSameType) {
            activeGameObject = activeGameObjectList.get(0);
/*

            if (registryLastPosition) {
                activeGameObjectLastPosition = new Vector2f(activeGameObject.transform.position);
                registryLastPosition = false;
            }
*/

            // Creates a Window
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (!activeGameObject.hasComponent(RigidBody2d.class)) {
                        activeGameObject.addComponent(new RigidBody2d());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (!activeGameObject.hasComponent(Box2DCollider.class)
                            && !activeGameObject.hasComponent(CircleCollider.class)) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (!activeGameObject.hasComponent(CircleCollider.class)
                            && !activeGameObject.hasComponent(Box2DCollider.class)) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                PropertiesWindow.addMenuItem(activeGameObject, "Add Ground", new Ground());

                ICustomPropertiesWindow customPropertiesWindow = SiriusTheFox.getWindow().getICustomPropertiesWindow();
                if (customPropertiesWindow != null) {
                    customPropertiesWindow.imgui(activeGameObject);
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        } /*else if (activeGameObjectList.isEmpty()) {
            activeGameObjectLastPosition.zero();
            registryLastPosition = true;
        }*/
    }

    public void addActiveGameObject(GameObject activeGameObject) {
        SpriteRenderer spriteRenderer = activeGameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            this.activeGameObjectOriginalColorList.add(new Vector4f(spriteRenderer.getColor()));
            spriteRenderer.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            // To keep the list indices the same with the game object
            this.activeGameObjectOriginalColorList.add(new Vector4f());
        }
        activeGameObjectList.add(activeGameObject);
    }

    public void clearSelected() {
        if (activeGameObjectOriginalColorList.size() > 0) {
            for (int i = 0; i < activeGameObjectList.size(); i++) {
                GameObject go = activeGameObjectList.get(i);
                SpriteRenderer spriteRenderer = go.getComponent(SpriteRenderer.class);
                if (spriteRenderer != null) {
                    spriteRenderer.setColor(activeGameObjectOriginalColorList.get(i));
                }
            }
        }

        activeGameObjectList.clear();
        activeGameObjectOriginalColorList.clear();
    }

    /**
     * Let add a customized component.
     *
     * @param customizedMsg The message that will appear in the menu box.
     * @param component The pretended component to add.
     */
    public static void addMenuItem(GameObject activeGameObject, String customizedMsg, Component component) {
        if (ImGui.menuItem(customizedMsg)) {
            if (!activeGameObject.hasComponent(component.getClass())) {
                activeGameObject.addComponent(component);
            }
        }
    }

    public static void addMenuItem(GameObject activeGameObject, Component component) {
        addMenuItem(activeGameObject, "Add" + component.getClass().getSimpleName(), component);
    }

    public GameObject getActiveGameObject() {
        return activeGameObjectList.size() == 1 ? this.activeGameObjectList.get(0) : null;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        if (activeGameObject != null) {
            clearSelected();
            activeGameObjectList.add(activeGameObject);
        }
    }

    /*public Vector2f getActiveGameObjectLastPosition() {
        return activeGameObjectLastPosition;
    }*/

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }

    public List<GameObject> getActiveGameObjectList() {
        return activeGameObjectList;
    }

    // Singleton
    public static PropertiesWindow get(PickingTexture pickingTexture) {
        if (instance == null)
            instance = new PropertiesWindow(pickingTexture);

        return instance;
    }
}
