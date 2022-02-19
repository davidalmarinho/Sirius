package gameobjects.components.editor;

import gameobjects.components.Component;
import jade.Window;
import jade.input.KeyListener;
import jade.rendering.spritesheet.Spritesheet;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

/**
 * For gizmos' stuff controlling purposes
 */
public class GizmoSystem extends Component {
    private final Spritesheet GIZMOS_SPRITESHEET;
    private transient int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmoSpritesheet) {
        GIZMOS_SPRITESHEET = gizmoSpritesheet;
    }

    @Override
    public void start() {
        gameObject.addComponent(new TranslateGizmo(GIZMOS_SPRITESHEET.getSprite(1),
                Window.get().getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(GIZMOS_SPRITESHEET.getSprite(2),
                Window.get().getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void editorUpdate(float dt) {
        // Attach and dis-attach gizmos according our calls
        if (usingGizmo == 0) {
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNoUsing();
        } else if (usingGizmo == 1) {
            gameObject.getComponent(TranslateGizmo.class).setNoUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        // Switch between Translate and Scale Gizmos
        if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
            usingGizmo = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            usingGizmo = 1;
        }
    }
}
