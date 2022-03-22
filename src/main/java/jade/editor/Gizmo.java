package jade.editor;

import gameobjects.GameObject;
import gameobjects.Prefabs;
import gameobjects.components.Component;
import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import jade.input.MouseListener;
import jade.rendering.Color;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

class Gizmo extends Component {
    private Color xAxisColor = new Color(1, 0.3f, 0.3f, 1);
    private Color xAxisColorHover = new Color(1, 0, 0, 1);
    private Color yAxisColor = new Color(0.3f, 1, 0.3f, 1);
    private Color yAxisColorHover = new Color(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;

    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;

    protected GameObject activeGameObject = null;

    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6.0f /80.0f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);

    private transient float gizmoWidth = 16f / 80f;
    private transient float gizmoHeight = 48f / 80f;

    protected boolean xAxisActive;
    protected boolean yAxisActive;

    private PropertiesWindow propertiesWindow;

    private boolean using;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xAxisSprite = xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getCurrentScene().addGameObject(xAxisObject);
        Window.getCurrentScene().addGameObject(yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90.0f;
        this.yAxisObject.transform.rotation = 180.0f;
        // TODO: 11/01/2022 Make Gizmos have the greatest zIndex by another way
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (using) setInactive();

        // xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
        // yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
    }

    @Override
    public void editorUpdate(float dt) {
        if (!using) return;

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null)
            setActive();
        else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Color());
        this.yAxisSprite.setColor(new Color());
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorld());
        if (mousePos.x <= xAxisObject.transform.position.x + gizmoHeight / 2.0f &&
                mousePos.x >= xAxisObject.transform.position.x - gizmoWidth / 2.0f &&
                mousePos.y >= xAxisObject.transform.position.y - gizmoHeight / 2.0f &&
                mousePos.y <= xAxisObject.transform.position.y + gizmoWidth / 2.0f) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorld());
        if (mousePos.x <= yAxisObject.transform.position.x  + gizmoWidth / 2.0f &&
                mousePos.x >= yAxisObject.transform.position.x - gizmoWidth / 2.0f &&
                mousePos.y <= yAxisObject.transform.position.y + gizmoHeight / 2.0f &&
                mousePos.y >= yAxisObject.transform.position.y - gizmoHeight / 2.0f) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void setNoUsing() {
        this.using = false;
        setInactive();
    }
}
