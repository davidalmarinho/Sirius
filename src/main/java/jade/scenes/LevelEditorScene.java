package jade.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import jade.gameobjects.GameObject;
import jade.gameobjects.Transform;
import jade.gameobjects.components.Sprite;
import jade.gameobjects.components.SpriteRenderer;
import jade.gameobjects.components.Spritesheet;
import jade.renderer.Camera;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        obj1.addComponent(SpriteRenderer.Builder.newInstance().setColor(1, 0, 0, 1).build());
        addGameObject(obj1);
        activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 0);
        obj2.addComponent(SpriteRenderer.Builder.newInstance()
                .setSprite(Sprite.Builder.newInstance()
                        .setTexture(AssetPool.getTexture("assets/images/blendImage2.png")).build())
                .build());
        addGameObject(obj2);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(obj1));

        Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
        Vector2f vec = new Vector2f(0.5f, 4.5f);
        String serialized = gson1.toJson(vec);
        System.out.println("Deserialized: " + gson1.fromJson(serialized, Vector2f.class));
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
        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Window test");
        ImGui.text("Hello World");
        ImGui.end();
    }
}