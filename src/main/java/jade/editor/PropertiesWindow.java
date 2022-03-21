package jade.editor;

import gameobjects.GameObject;
import imgui.ImGui;
import jade.rendering.PickingTexture;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2d;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private List<GameObject> activeGameObjectList;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjectList = new ArrayList<>();
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
        activeGameObjectList.add(activeGameObject);
    }

    public void clearSelected() {
        activeGameObjectList.clear();
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
