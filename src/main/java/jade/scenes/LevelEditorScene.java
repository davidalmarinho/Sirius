package jade.scenes;

import imgui.ImGui;
import jade.gameobjects.GameObject;
import jade.gameobjects.Transform;
import jade.gameobjects.components.*;
import jade.renderer.Camera;
import jade.renderer.spritesheet.Spritesheet;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {
    private Spritesheet sprites;
    private GameObject obj1;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector3f(-250, 0, 1));

        // We have a level already created, so we don't want to create a new one
        if (levelLoaded) {
            if (gameObjectList.size() > 0) {
                this.activeGameObject = gameObjectList.get(0);
            }
            return;
        }

        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        obj1.addComponent(SpriteRenderer.Builder.newInstance().setColor(1, 0, 0, 1).build());
        obj1.addComponent(new RigidBody());
        addGameObject(obj1);
        activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 3);
        obj2.addComponent(SpriteRenderer.Builder.newInstance()
                .setSprite(Sprite.Builder.newInstance()
                        .setTexture(AssetPool.getTexture("assets/images/blendImage2.png")).build())
                .build());
        addGameObject(obj2);
    }

    @Override
    public void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(
                        AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");

        // Get the texture that was already loaded after saving the saving file with Gson
        for (GameObject g : gameObjectList) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
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

    @Override
    public void imgui() {
        ImGui.begin("Icos");



        ImGui.end();
    }
}