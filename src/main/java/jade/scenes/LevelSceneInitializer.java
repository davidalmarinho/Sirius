package jade.scenes;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import jade.animations.StateMachine;
import jade.rendering.spritesheet.Images;
import jade.rendering.spritesheet.Spritesheet;
import jade.utils.AssetPool;

public class LevelSceneInitializer implements ISceneInitializer {
    @Override
    public void init(Scene scene) {

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

        AssetPool.addSpritesheet(Images.TURTLE.getSpritesheet(),
                new Spritesheet(AssetPool.getTexture(Images.TURTLE.getSpritesheet()),
                        16, 24, 4, 0));

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

    }

    @Override
    public ISceneInitializer build() {
        return new LevelSceneInitializer();
    }
}
