package gameobjects;

import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import jade.animations.AnimationState;
import jade.animations.StateMachine;
import jade.rendering.spritesheet.Spritesheet;
import jade.utils.AssetPool;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getCurrentScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer spriteRenderer = SpriteRenderer.Builder.newInstance().setSprite(sprite).build();
        block.addComponent(spriteRenderer);

        return block;
    }

    public static GameObject generateMario(Sprite sprite, float sizeX, float sizeY) {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");

        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionMarkBlock(Sprite sprite, float sizeX, float sizeY) {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");

        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState();
        run.title = "Flicker";
        float defaultFrameTime = 0.23f;
        run.addFrame(items.getSprite(0), defaultFrameTime);
        run.addFrame(items.getSprite(1), defaultFrameTime);
        run.addFrame(items.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }
}
