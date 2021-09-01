package jade.scenes;

import jade.gameobjects.GameObject;
import jade.gameobjects.Transform;
import jade.gameobjects.components.SpriteRenderer;
import jade.gameobjects.components.Spritesheet;
import jade.renderer.Camera;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {
    private Spritesheet sprites;
    private GameObject obj1;
    private final float FLIP_TIME = 0.2f;
    private float flip_time_left = 0.0f;
    private int spriteIndex = 0;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector3f(-250, 0, 1));

        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        addGameObject(obj1);

        // GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        // obj2.addComponent(new SpriteRenderer(sprites.getSprite(15)));
        // addGameObject(obj2);
    }

    @Override
    public void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(
                        AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
    }

    @Override
    public void update(float dt) {
        flip_time_left -= 0.5f * dt;
        if (flip_time_left <= 0.0f) {
            flip_time_left = FLIP_TIME;
            spriteIndex++;
            if (spriteIndex > 7) {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }

        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }
}