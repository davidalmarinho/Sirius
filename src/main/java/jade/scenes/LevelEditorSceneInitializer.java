package jade.scenes;

import gameobjects.ICustomPrefabs;
import gameobjects.Prefabs;
import imgui.ImGui;
import imgui.ImVec2;
import gameobjects.GameObject;
import gameobjects.components.*;
import jade.SiriusTheFox;
import jade.Sound;
import jade.animations.StateMachine;
import jade.editor.*;
import jade.editor.components.KeyControls;
import jade.rendering.spritesheet.Images;
import jade.rendering.spritesheet.Spritesheet;
import jade.utils.AssetPool;
import org.joml.Vector2f;

import java.io.File;
import java.util.Collection;

/**
 * Logic to edit levels
 */
public class LevelEditorSceneInitializer implements ISceneInitializer {
    private Spritesheet sprites;
    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpritesheet(Images.DECORATIONS_AND_BLOCKS.getSpritesheet());
        Spritesheet gizmos = AssetPool.getSpritesheet(Images.GIZMOS.getTexture());

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new MouseControls());

        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));

        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObject(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet(Images.DECORATIONS_AND_BLOCKS.getSpritesheet(),
                new Spritesheet(
                        AssetPool.getTexture(Images.DECORATIONS_AND_BLOCKS.getSpritesheet()),
                        16, 16, 81, 0));

        AssetPool.addSpritesheet(Images.SPRITE_SHEET.getSpritesheet(),
                new Spritesheet(
                        AssetPool.getTexture(Images.SPRITE_SHEET.getSpritesheet()),
                        16, 16, 26, 0));

        AssetPool.addSpritesheet(Images.ITEMS.getSpritesheet(),
                new Spritesheet(
                        AssetPool.getTexture(Images.ITEMS.getSpritesheet()),
                        16, 16, 43, 0));

        AssetPool.addSpritesheet(Images.PLAYER_BIG_SPRITE_SHEET.getSpritesheet(),
                new Spritesheet(AssetPool.getTexture(Images.PLAYER_BIG_SPRITE_SHEET.getSpritesheet()),
                        16, 32, 42, 0));

        AssetPool.addSpritesheet(Images.PIPES.getSpritesheet(),
                new Spritesheet(AssetPool.getTexture(Images.PIPES.getSpritesheet()),
                        32, 32, 4, 0));

        AssetPool.addSpritesheet(Images.GIZMOS.getTexture(),
                new Spritesheet(
                        AssetPool.getTexture(Images.GIZMOS.getTexture()),
                        24, 48, 3, 0));

        AssetPool.getTexture(Images.BLEND_IMAGE_2.getTexture());



        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        // Get the texture that was already loaded after saving the saving file with Gson
        for (GameObject g : scene.getGameObjectList()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }

            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }

    }

    @Override
    public void imgui() {
        // ================
        // For debug purposes
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();
        // ================

        ImGui.begin("Objects");

        if (ImGui.beginTabBar("TabBar")) {
            if (ImGui.beginTabItem("Icons")) {
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
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
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
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
                        ImGui.sameLine();

                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Prefabs")) {

                // Check if we have customized prefabs
                ICustomPrefabs customPrefabs = SiriusTheFox.getWindow().getICustomPrefabs();
                if (customPrefabs != null) {
                    customPrefabs.imgui();
                }
                Prefabs.uid = 0;
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilePath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying())
                            sound.play();
                        else
                            sound.stop();
                    }
                }

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }

    public GameObject getLevelEditorStuff() {
        return levelEditorStuff;
    }
}