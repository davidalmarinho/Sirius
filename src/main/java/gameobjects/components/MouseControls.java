package gameobjects.components;

import gameobjects.GameObject;
import jade.Window;
import jade.input.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingGameObject = null;

    public void pickupObject(GameObject go) {
        holdingGameObject = go;
        Window.getCurrentScene().addGameObject(go);
    }

    public void place() {
        holdingGameObject = null;
    }

    @Override
    public void update(float dt) {
        if (holdingGameObject != null) {
            holdingGameObject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingGameObject.transform.position.y = MouseListener.getOrthoY() - 16;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}
