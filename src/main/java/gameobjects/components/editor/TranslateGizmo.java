package gameobjects.components.editor;

import gameobjects.GameObject;
import gameobjects.Prefabs;
import gameobjects.components.Component;
import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import jade.input.MouseListener;
import jade.rendering.Color;
import org.joml.Vector2f;

public class TranslateGizmo extends Component {
    private Color xAxisColor = new Color(1, 0.3f, 0.3f, 1);
    private Color xAxisColorHover = new Color(1, 0, 0, 1);
    private Color yAxisColor = new Color(0.3f, 1, 0.3f, 1);
    private Color yAxisColorHover = new Color(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;

    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;

    private GameObject activeGameObject;

    private Vector2f xAxisOffset = new Vector2f(64.0f, 5.0f);
    private Vector2f yAxisOffset = new Vector2f(23.0f, 63.0f);

    private int gizmoWidth = 16;
    private int gizmoHeight = 48;

    private PropertiesWindow propertiesWindow;

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
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
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null) {
            setActive();
        } else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

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
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x &&
                mousePos.x >= xAxisObject.transform.position.x - gizmoHeight &&
                mousePos.y >= xAxisObject.transform.position.y &&
                mousePos.y <= xAxisObject.transform.position.y + gizmoWidth) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x &&
                mousePos.x >= yAxisObject.transform.position.x - gizmoWidth &&
                mousePos.y <= yAxisObject.transform.position.y &&
                mousePos.y >= yAxisObject.transform.position.y - gizmoHeight) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }
}
