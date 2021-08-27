package jade.scenes;

import jade.gameobjects.GameObject;
import jade.gameobjects.Transform;
import jade.gameobjects.components.SpriteRenderer;
import jade.renderer.Camera;
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
        this.camera = new Camera(new Vector3f());

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float) (600 - xOffset * 2);
        float totalHeight = (float) (300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xpos = xOffset + (x * sizeX);
                float ypos = yOffset + (y * sizeY);

                GameObject go = new GameObject("Obj" + x + "" + y, new Transform(new Vector2f(xpos, ypos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xpos / totalWidth, ypos / totalHeight, 1, 1)));
                this.addGameObject(go);
            }
        }
    }

    @Override
    public void update(float dt) {
        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }
}