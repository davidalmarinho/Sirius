package gameobjects.components.editor;

import gameobjects.components.Component;
import jade.input.MouseListener;
import jade.rendering.Camera;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component {

    private float dragDebounce = 0.032f;

    private final Camera LEVEL_EDITOR_CAMERA;
    private Vector2f clickOrigin;

    public EditorCamera(Camera LEVEL_EDITOR_CAMERA) {
        this.LEVEL_EDITOR_CAMERA = LEVEL_EDITOR_CAMERA;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt) {
        // Moving camera
        final int BUTTON = GLFW_MOUSE_BUTTON_MIDDLE;
        if (MouseListener.mouseButtonDown(BUTTON) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(BUTTON)) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());

            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            final float DRAG_SENSIBILITY = 30.0f;
            LEVEL_EDITOR_CAMERA.position.sub(delta.mul(dt).mul(DRAG_SENSIBILITY));

            // Lerp
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(BUTTON)) {
            dragDebounce = 0.032f;
        }
    }
}
