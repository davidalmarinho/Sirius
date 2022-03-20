package jade.editor;

import gameobjects.GameObject;
import imgui.ImGui;
import jade.input.MouseListener;
import jade.rendering.PickingTexture;
import jade.scenes.Scene;
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

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjectList = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounce -= dt;
        if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixed(x, y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && !pickedObj.hasComponent(NonPickable.class)) {
                activeGameObject = pickedObj;
                setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                activeGameObject = null;
            }
            this.debounce = 0.2f;
        }
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

    public List<GameObject> getActiveGameObjectList() {
        return activeGameObjectList;
    }
}
