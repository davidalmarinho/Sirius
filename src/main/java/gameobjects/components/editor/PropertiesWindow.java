package gameobjects.components.editor;

import gameobjects.GameObject;
import imgui.ImGui;
import jade.Window;
import jade.input.MouseListener;
import jade.rendering.PickingTexture;
import jade.scenes.Scene;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2d;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounce -= dt;
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixed(x, y);
            GameObject pickedObj = Window.getCurrentScene().getGameObject(gameObjectId);
            // activeGameObject = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                activeGameObject = pickedObj;
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                activeGameObject = null;
            }
            this.debounce = 0.2f;
        }
    }

    public void imgui() {
        if (activeGameObject != null) {
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

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        this.activeGameObject = activeGameObject;
    }
}
