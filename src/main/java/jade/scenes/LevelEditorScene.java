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
import org.joml.Vector2f;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {
    private Spritesheet sprites;
    private final GameObject levelEditorStuff = this.createGameObject("LevelEditor");

    public LevelEditorScene() {

    }

    @Override
    public void init() {
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
        DebugDraw.addBox2D(new Vector2f(400, 200), new Vector2f(64, 32), 30.0f, Color.GREEN, 1);
        DebugDraw.addBox2D(new Vector2f(400, 200), new Vector2f(64, 32), 0.0f, Color.BLUE, 1);
        DebugDraw.addCircle(new Vector2f(600, 400), 64, Color.BLUE, 1);

        for (GameObject go : gameObjectList) {
            go.update(dt);
        }
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