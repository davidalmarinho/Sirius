package sirius.scenes;

import gameobjects.ICustomPrefabs;
import gameobjects.Prefabs;
import imgui.ImGui;
import gameobjects.GameObject;
import gameobjects.components.*;
import sirius.SiriusTheFox;
import sirius.Sound;
import sirius.animations.StateMachine;
import sirius.editor.*;
import sirius.editor.components.KeyControls;
import sirius.editor.imgui.JImGui;
import sirius.rendering.spritesheet.Images;
import sirius.rendering.spritesheet.Spritesheet;
import sirius.utils.AssetPool;

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
        // Stop all sounds that were playing before
        for (Sound sound : AssetPool.getAllSounds())
            sound.stop();

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

        AssetPool.addSpritesheet(Images.TURTLE.getSpritesheet(),
                new Spritesheet(AssetPool.getTexture(Images.TURTLE.getSpritesheet()),
                        16, 24, 4, 0));

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
        AssetPool.addSound("assets/sounds/fireball.ogg", false);

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
                if (JImGui.spritesLayout(sprites)) {
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(JImGui.getSelectedGameObject());
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

    @Override
    public ISceneInitializer build() {
        return new LevelSceneInitializer();
    }

    public GameObject getLevelEditorStuff() {
        return levelEditorStuff;
    }
}