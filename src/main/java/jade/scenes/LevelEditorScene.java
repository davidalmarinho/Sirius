package jade.scenes;

import jade.gameobjects.GameObject;
import jade.gameobjects.Transform;
import jade.gameobjects.components.SpriteRenderer;
import jade.renderer.Camera;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
        
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(-250, 0, 1));

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage.png")));
        addGameObject(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage2.png")));
        addGameObject(obj2);

        loadResources();
    }

    @Override
    public void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }
}