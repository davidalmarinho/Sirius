package gameobjects.components.editor;

import gameobjects.GameObject;
import gameobjects.Prefabs;
import gameobjects.components.Component;
import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import jade.rendering.Color;
import jade.rendering.spritesheet.Images;

public class TranslateGizmo extends Component {
    private final Color X_AXIS_COLOR = new Color(1, 0, 0 ,1);
    private final Color Y_AXIS_COLOR = new Color(0, 0, 1, 1);
    private Color xAxisColorMove = new Color();
    private Color yAxisColorMove = new Color();

    private GameObject xAxisObject;
    private GameObject yAxisObject;

    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;

    private GameObject activeGameObject;

    private PropertiesWindow propertiesWindow;

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.xAxisSprite = xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        Window.getCurrentScene().addGameObject(xAxisObject);
        Window.getCurrentScene().addGameObject(yAxisObject);
    }

    @Override
    public void start() {

    }

    @Override
    public void update(float dt) {
        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
        }

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null) {
            setActive();
        } else {
            setInactive();
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(X_AXIS_COLOR);
        this.yAxisSprite.setColor(Y_AXIS_COLOR);
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Color());
        this.yAxisSprite.setColor(new Color());
    }
}
