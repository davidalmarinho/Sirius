package jade.editor;

import gameobjects.GameObject;
import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import jade.animations.StateMachine;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.rendering.Color;
import jade.utils.Settings;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    GameObject holdingGameObject = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    public void pickupObject(GameObject go) {
        if (holdingGameObject != null) holdingGameObject.destroy();

        holdingGameObject = go;
        holdingGameObject.getComponent(SpriteRenderer.class).setColor(new Color(0.8f, 0.8f, 0.8f, 0.8f));
        this.holdingGameObject.addComponent(new NonPickable());
        Window.getCurrentScene().addGameObject(go);
    }

    public void place() {
        GameObject newObj = this.holdingGameObject.copy();

        if (newObj.hasComponent(StateMachine.class)) newObj.getComponent(StateMachine.class).refreshTextures();

        newObj.getComponent(SpriteRenderer.class).setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        newObj.removeComponent(NonPickable.class);
        Window.getCurrentScene().addGameObject(newObj);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        if (holdingGameObject != null && debounce <= 0) {
            holdingGameObject.transform.position.x = MouseListener.getWorld().x;
            holdingGameObject.transform.position.y = MouseListener.getWorld().y;

            holdingGameObject.transform.position.x = ((int) Math.floor(holdingGameObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingGameObject.transform.position.y = ((int) Math.floor(holdingGameObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
                debounce = debounceTime;
            }

            if (KeyListener.isKeyDown(GLFW_KEY_ESCAPE) || KeyListener.isKeyDown(GLFW_KEY_DELETE)) {
                holdingGameObject.destroy();
                holdingGameObject = null;
            }
        }
    }
}
