package sirius.scenes;

import gameobjects.GameObject;
import gameobjects.components.*;
import sirius.SiriusTheFox;
import sirius.Sound;
import sirius.animations.StateMachine;
import sirius.editor.*;
import sirius.editor.components.AutoRecompile;
import sirius.editor.components.KeyControls;
import sirius.rendering.spritesheet.Images;
import sirius.rendering.spritesheet.Spritesheet;
import sirius.utils.Pool;

/**
 * Logic to edit levels
 */
public class LevelEditorSceneInitializer implements ISceneInitializer {
    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {
        // Stop all sounds that were playing before
        for (Sound sound : Pool.Assets.getAllSounds())
            sound.stop();

        SiriusTheFox.getImGuiLayer().getTabBar()
                .loadSpritesheet(Pool.Assets.getSpritesheet(Images.DECORATIONS_AND_BLOCKS.getSpritesheet()));
        Spritesheet gizmos = Pool.Assets.getSpritesheet(Images.GIZMOS.getTexture());

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new MouseControls());

        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));

        levelEditorStuff.addComponent(new GizmoSystem(gizmos));

        levelEditorStuff.addComponent(new AutoRecompile());
        scene.addGameObject(levelEditorStuff);
    }

    // TODO: 07/07/2022 Abstract this
    @Override
    public void loadResources(Scene scene) {
        Pool.Assets.getShader("assets/shaders/default.glsl");

        Pool.Assets.addSpritesheet("assets/images/tools/tools.png",
                new Spritesheet(
                        Pool.Assets.getTexture("assets/images/tools/tools.png"),
                        64, 64, 2, 0));

        Pool.Assets.addSpritesheet(Images.DECORATIONS_AND_BLOCKS.getSpritesheet(),
                new Spritesheet(
                        Pool.Assets.getTexture(Images.DECORATIONS_AND_BLOCKS.getSpritesheet()),
                        16, 16, 81, 0));

        Pool.Assets.addSpritesheet(Images.SPRITE_SHEET.getSpritesheet(),
                new Spritesheet(
                        Pool.Assets.getTexture(Images.SPRITE_SHEET.getSpritesheet()),
                        16, 16, 26, 0));

        Pool.Assets.addSpritesheet(Images.ITEMS.getSpritesheet(),
                new Spritesheet(
                        Pool.Assets.getTexture(Images.ITEMS.getSpritesheet()),
                        16, 16, 43, 0));

        Pool.Assets.addSpritesheet(Images.PLAYER_BIG_SPRITE_SHEET.getSpritesheet(),
                new Spritesheet(Pool.Assets.getTexture(Images.PLAYER_BIG_SPRITE_SHEET.getSpritesheet()),
                        16, 32, 42, 0));

        Pool.Assets.addSpritesheet(Images.PIPES.getSpritesheet(),
                new Spritesheet(Pool.Assets.getTexture(Images.PIPES.getSpritesheet()),
                        32, 32, 4, 0));

        Pool.Assets.addSpritesheet(Images.TURTLE.getSpritesheet(),
                new Spritesheet(Pool.Assets.getTexture(Images.TURTLE.getSpritesheet()),
                        16, 24, 4, 0));

        Pool.Assets.addSpritesheet(Images.GIZMOS.getTexture(),
                new Spritesheet(
                        Pool.Assets.getTexture(Images.GIZMOS.getTexture()),
                        24, 48, 3, 0));

        Pool.Assets.getTexture(Images.BLEND_IMAGE_2.getTexture());

        Pool.Assets.addSound("assets/sounds/main-theme-overworld.ogg", true);
        Pool.Assets.addSound("assets/sounds/flagpole.ogg", false);
        Pool.Assets.addSound("assets/sounds/break_block.ogg", false);
        Pool.Assets.addSound("assets/sounds/bump.ogg", false);
        Pool.Assets.addSound("assets/sounds/coin.ogg", false);
        Pool.Assets.addSound("assets/sounds/gameover.ogg", false);
        Pool.Assets.addSound("assets/sounds/jump-small.ogg", false);
        Pool.Assets.addSound("assets/sounds/mario_die.ogg", false);
        Pool.Assets.addSound("assets/sounds/pipe.ogg", false);
        Pool.Assets.addSound("assets/sounds/powerup.ogg", false);
        Pool.Assets.addSound("assets/sounds/powerup_appears.ogg", false);
        Pool.Assets.addSound("assets/sounds/stage_clear.ogg", false);
        Pool.Assets.addSound("assets/sounds/stomp.ogg", false);
        Pool.Assets.addSound("assets/sounds/kick.ogg", false);
        Pool.Assets.addSound("assets/sounds/invincible.ogg", false);
        Pool.Assets.addSound("assets/sounds/fireball.ogg", false);

        // Get the texture that was already loaded after saving the saving file with Gson
        for (GameObject g : scene.getGameObjectList()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(Pool.Assets.getTexture(spr.getTexture().getFilePath()));
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
        // ImGui.begin("Level Editor Stuff");
        // levelEditorStuff.imgui();
        // ImGui.end();
        // ================
    }

    @Override
    public ISceneInitializer build() {
        return new LevelSceneInitializer();
    }

    public GameObject getLevelEditorStuff() {
        return levelEditorStuff;
    }
}