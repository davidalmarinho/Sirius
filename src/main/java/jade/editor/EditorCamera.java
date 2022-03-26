package jade.editor;

import gameobjects.components.Component;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.rendering.Camera;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private transient float dragDebounce = 0.032f;

    private final Camera LEVEL_EDITOR_CAMERA;
    private Vector2f clickOrigin;

    private final float SCROLL_SENSITIVITY = 0.1f;
    private final float DRAG_SENSIBILITY = 30.0f;
    private boolean reset;

    private transient float lerpTime = 0.0f;

    public EditorCamera(Camera LEVEL_EDITOR_CAMERA) {
        this.LEVEL_EDITOR_CAMERA = LEVEL_EDITOR_CAMERA;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        // Moving camera
        final int BUTTON = GLFW_MOUSE_BUTTON_MIDDLE;
        if (MouseListener.isMouseButtonPressed(BUTTON) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getWorld());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.isMouseButtonPressed(BUTTON)) {
            Vector2f mousePos = new Vector2f(MouseListener.getWorld());

            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            LEVEL_EDITOR_CAMERA.position.sub(delta.mul(dt).mul(DRAG_SENSIBILITY));

            // Lerp
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.isMouseButtonPressed(BUTTON)) {
            dragDebounce = 0.032f;
        }

        // Zoom in and zoom out
        if (MouseListener.getScreenY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * SCROLL_SENSITIVITY),
                    1 / LEVEL_EDITOR_CAMERA.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            LEVEL_EDITOR_CAMERA.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_0)) {
            reset = true;
        }

        if (reset) {
            LEVEL_EDITOR_CAMERA.position.lerp(new Vector2f(-250.0f, 0.0f), lerpTime);
            LEVEL_EDITOR_CAMERA.setZoom(LEVEL_EDITOR_CAMERA.getZoom()
                    + ((1.0f - LEVEL_EDITOR_CAMERA.getZoom())) * lerpTime);
            lerpTime += 0.1f * dt;
            if ((LEVEL_EDITOR_CAMERA.position.x) <= -245.0f && Math.abs(LEVEL_EDITOR_CAMERA.position.y) <= 5.0f) {
                lerpTime = 0.0f;
                LEVEL_EDITOR_CAMERA.position.set(-250.0f, 0.0f);
                LEVEL_EDITOR_CAMERA.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
