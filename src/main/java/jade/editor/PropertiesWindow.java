package jade.editor;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import jade.rendering.Color;
import jade.rendering.PickingTexture;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2d;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private List<GameObject> activeGameObjectList;
    private GameObject activeGameObject = null;
    private List<Vector4f> activeGameObjectOriginalColorList;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjectList = new ArrayList<>();
        this.activeGameObjectOriginalColorList = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {
        if (activeGameObjectList.size() == 1 && activeGameObjectList.get(0) != null) {
            activeGameObject = activeGameObjectList.get(0);
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

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
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
        if (activeGameObjectList.size() > 0) {
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

    public void setInactive() {

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

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }

    public List<GameObject> getActiveGameObjectList() {
        return activeGameObjectList;
    }
}
