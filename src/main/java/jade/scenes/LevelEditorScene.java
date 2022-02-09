package jade.scenes;

import gameobjects.Prefabs;
import gameobjects.components.editor.*;
import imgui.ImGui;
import imgui.ImVec2;
import gameobjects.GameObject;
import gameobjects.components.*;
import jade.rendering.Camera;
import jade.rendering.Color;
import jade.rendering.debug.DebugDraw;
import jade.rendering.spritesheet.Images;
import jade.rendering.spritesheet.Spritesheet;
import jade.utils.AssetPool;
import jdk.nashorn.internal.runtime.Debug;
import org.joml.Vector2f;
import physics2d.PhysicsSystem2D;
import physics2d.primitives.Circle;
import physics2d.rigidBody.RigidBody2D;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {
    private Spritesheet sprites;
    private final GameObject levelEditorStuff = this.createGameObject("LevelEditor");
    PhysicsSystem2D physicsSystem2D = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0, -10));
    Transform obj1, obj2;
    RigidBody2D rb1, rb2;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        obj1 = new Transform(new Vector2f(100, 500), 1.0f, 1);
        obj2 = new Transform(new Vector2f(100, 300), 1.0f, 1);
        rb1 = new RigidBody2D();
        rb2 = new RigidBody2D();
        rb1.setRawTransform(obj1);
        rb2.setRawTransform(obj2);
        rb1.setMass(100.0f);
        rb2.setMass(200.0f);

        Circle c1 = new Circle();
        c1.setRadius(10.0f);
        c1.setRigidBody2D(rb1);
        Circle c2 = new Circle();
        c2.setRadius(20.0f);
        c2.setRigidBody2D(rb2);
        rb1.setCollider(c1);
        rb2.setCollider(c2);

        physicsSystem2D.addRigidBody2D(rb1, true);
        physicsSystem2D.addRigidBody2D(rb2, false);

        loadResources();
        sprites = AssetPool.getSpritesheet(Images.DECORATIONS_AND_BLOCKS.getSpritesheet());
        Spritesheet gizmos = AssetPool.getSpritesheet(Images.GIZMOS.getTexture());

        this.camera = new Camera(new Vector2f(-250, 0));
        levelEditorStuff.addComponent(new MouseControls());

        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(camera));

        levelEditorStuff.addComponent(new GizmoSystem(gizmos));

        levelEditorStuff.start();
    }

    @Override
    public void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet(Images.DECORATIONS_AND_BLOCKS.getSpritesheet(),
                new Spritesheet(
                        AssetPool.getTexture(Images.DECORATIONS_AND_BLOCKS.getSpritesheet()),
                        16, 16, 81, 0));
        AssetPool.addSpritesheet(Images.GIZMOS.getTexture(),
                new Spritesheet(
                        AssetPool.getTexture(Images.GIZMOS.getTexture()),
                        24, 48, 3, 0));
        AssetPool.getTexture(Images.BLEND_IMAGE_2.getTexture());

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
        levelEditorStuff.update(dt);
        camera.adjustProjection();

        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        DebugDraw.addCircle(obj1.position, 10.0f, Color.DARK_GREEN);
        DebugDraw.addCircle(obj2.position, 20.0f, Color.BLUE);
        physicsSystem2D.update(dt);
    }

    @Override
    public void render() {
        this.renderer.render();
    }

    @Override
    public void imgui() {
        // ================
        // For debug purposes
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();
        // ================

        ImGui.begin("Icons");

        // Gets the window's positions
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        // Gets the window's size
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        // Gets item's spacing
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;

            int id = sprite.getTextureID();
            Vector2f[] texCoords = sprite.getTextureCoordinates();

            // Each texture has the spritesheet id, so all textures have the same id, so there is needed to pushID()
            ImGui.pushID(i);

            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, sprite.getWidth() * 2, sprite.getHeight() * 2);
                // Attach object to the mouse cursor
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }

            // After we don't want to worry about that we have changed textures' id, so let's replace it again
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            // Keep in the same line if we still have items and if the current item isn't bigger than the window itself
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}