package main;

import components.*;
import gameobjects.GameObject;
import gameobjects.ICustomPrefabs;
import gameobjects.Prefabs;
import gameobjects.components.Sprite;
import gameobjects.components.game_components.Ground;
import jade.animations.AnimationState;
import jade.animations.StateMachine;
import jade.rendering.spritesheet.Spritesheet;;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import physics2d.BodyTypes;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2d;

import static gameobjects.Prefabs.generateSpriteObject;

public class CustomPrefabs implements ICustomPrefabs {

    public static GameObject generateMario(Sprite sprite, float sizeX, float sizeY) {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");
        Spritesheet bigPlayerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), sizeX, sizeY);

        // Little mario animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addState(run.title, switchDirection.title, "switchDirection");
        stateMachine.addState(run.title, idle.title, "stopRunning");
        stateMachine.addState(run.title, jump.title, "jump");
        stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addState(switchDirection.title, run.title, "startRunning");
        stateMachine.addState(switchDirection.title, jump.title, "jump");
        stateMachine.addState(idle.title, run.title, "startRunning");
        stateMachine.addState(idle.title, jump.title, "jump");
        stateMachine.addState(jump.title, idle.title, "stopJumping");

        stateMachine.addState(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addState(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigRun.title, bigJump.title, "jump");
        stateMachine.addState(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addState(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addState(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addState(bigIdle.title, bigJump.title, "jump");
        stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireRun.title, fireJump.title, "jump");
        stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addState(fireIdle.title, fireJump.title, "jump");
        stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addState(run.title, bigRun.title, "powerup");
        stateMachine.addState(idle.title, bigIdle.title, "powerup");
        stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addState(jump.title, bigJump.title, "powerup");
        stateMachine.addState(bigRun.title, fireRun.title, "powerup");
        stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addState(bigJump.title, fireJump.title, "powerup");

        stateMachine.addState(bigRun.title, run.title, "damage");
        stateMachine.addState(bigIdle.title, idle.title, "damage");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addState(bigJump.title, jump.title, "damage");
        stateMachine.addState(fireRun.title, bigRun.title, "damage");
        stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addState(fireJump.title, bigJump.title, "damage");

        stateMachine.addState(run.title, die.title, "die");
        stateMachine.addState(switchDirection.title, die.title, "die");
        stateMachine.addState(idle.title, die.title, "die");
        stateMachine.addState(jump.title, die.title, "die");
        stateMachine.addState(bigRun.title, run.title, "die");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addState(bigIdle.title, idle.title, "die");
        stateMachine.addState(bigJump.title, jump.title, "die");
        stateMachine.addState(fireRun.title, bigRun.title, "die");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addState(fireIdle.title, bigIdle.title, "die");
        stateMachine.addState(fireJump.title, bigJump.title, "die");
        mario.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        pb.setSize(0.39f, 0.31f);

        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.DYNAMIC);
        rigidBody2d.setContinuousCollision(false);
        rigidBody2d.setFixedRotation(true);
        rigidBody2d.setMass(25.0f);

        mario.addComponent(rigidBody2d);
        mario.addComponent(pb);

        return mario;
    }

    public static GameObject generateQuestionMarkBlock(Sprite sprite, float xSize, float ySize) {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");

        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState flicker = new AnimationState();
        flicker.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(items.getSprite(0), defaultFrameTime);
        flicker.addFrame(items.getSprite(1), defaultFrameTime);
        flicker.addFrame(items.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        AnimationState inactive = new AnimationState();
        inactive.title = "Inactive";
        inactive.addFrame(items.getSprite(3), 0.1f);
        inactive.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(flicker.title);
        stateMachine.addState(flicker.title, inactive.title, "setInactive");
        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        RigidBody2d rb = new RigidBody2d();
        rb.setBodyType(BodyTypes.STATIC);
        questionBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(0.25f, 0.25f);
        questionBlock.addComponent(b2d);
        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateCoin() {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");

        GameObject coin = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());

        coin.addComponent(new BlockCoin());

        return coin;
    }

    public static GameObject generateMushroom(Sprite sprite, float xSize, float ySize) {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");

        GameObject mushroom = generateSpriteObject(items.getSprite(10), 0.25f, 0.25f);

        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.DYNAMIC);
        rigidBody2d.setFixedRotation(true);
        rigidBody2d.setContinuousCollision(false);
        mushroom.addComponent(rigidBody2d);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        mushroom.addComponent(circleCollider);

        mushroom.addComponent(new MushroomAI());

        return mushroom;
    }

    public static GameObject generateFlower(Sprite sprite, float xSize, float ySize) {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");

        GameObject flower = generateSpriteObject(items.getSprite(20), 0.25f, 0.25f);

        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.STATIC);
        rigidBody2d.setFixedRotation(true);
        rigidBody2d.setContinuousCollision(false);
        flower.addComponent(rigidBody2d);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        flower.addComponent(circleCollider);

        flower.addComponent(new Flower());

        return flower;
    }

    public static GameObject generateGoomba(Sprite sprite, float xSize, float ySize) {
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");

        GameObject goomba = generateSpriteObject(spritesheet.getSprite(15), 0.25f, 0.25f);

        AnimationState walk = new AnimationState();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(spritesheet.getSprite(14), defaultFrameTime);
        walk.addFrame(spritesheet.getSprite(15), defaultFrameTime);
        walk.setLoop(true);

        AnimationState squashed = new AnimationState();
        squashed.title = "Squashed";
        squashed.addFrame(spritesheet.getSprite(16), 0.1f);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(squashed);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addState(walk.title, squashed.title, "squashMe");
        goomba.addComponent(stateMachine);

        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.DYNAMIC);
        rigidBody2d.setMass(15.0f);
        rigidBody2d.setFixedRotation(true);
        goomba.addComponent(rigidBody2d);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.12f);
        goomba.addComponent(circleCollider);

        goomba.addComponent(new GoombaAI());

        return goomba;
    }

    private static GameObject generatePipeDown(Sprite sprite, float xSize, float ySize) {
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png");

        GameObject pipe = generateSpriteObject(spritesheet.getSprite(0), 0.5f, 0.5f);

        return generatePipe(pipe, Direction.DOWN);
    }

    private static GameObject generatePipeUp(Sprite sprite, float xSize, float ySize) {
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png");

        GameObject pipe = generateSpriteObject(spritesheet.getSprite(1), 0.5f, 0.5f);

        return generatePipe(pipe, Direction.UP);

    }

    private static GameObject generatePipeRight(Sprite sprite, float xSize, float ySize) {
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png");

        GameObject pipe = generateSpriteObject(spritesheet.getSprite(2), 0.5f, 0.5f);

        return generatePipe(pipe, Direction.RIGHT);
    }

    private static GameObject generatePipeLeft(Sprite sprite, float xSize, float ySize) {
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png");

        GameObject pipe = generateSpriteObject(spritesheet.getSprite(3), 0.5f, 0.5f);

        return generatePipe(pipe, Direction.LEFT);
    }

    private static GameObject generatePipe(GameObject pipe, Direction direction) {
        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.STATIC);
        rigidBody2d.setFixedRotation(true);
        rigidBody2d.setContinuousCollision(false);
        pipe.addComponent(rigidBody2d);

        Box2DCollider box2DCollider = new Box2DCollider();
        box2DCollider.setHalfSize(new Vector2f(0.5f, 0.5f));
        pipe.addComponent(box2DCollider);
        pipe.addComponent(new Pipe(direction));
        pipe.addComponent(new Ground());

        return pipe;
    }

    public static GameObject generateTurtle(Sprite sprite, float xSize, float ySize) {
        Spritesheet turtleSpritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/turtle.png");
        GameObject turtle = generateSpriteObject(turtleSpritesheet.getSprite(0), 0.25f, 0.35f);

        AnimationState walk = new AnimationState();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(turtleSpritesheet.getSprite(0), defaultFrameTime);
        walk.addFrame(turtleSpritesheet.getSprite(1), defaultFrameTime);
        walk.setLoop(true);

        AnimationState turtleShell = new AnimationState();
        turtleShell.title = "TurtleShellSpin";
        turtleShell.addFrame(turtleSpritesheet.getSprite(2), 0.1f);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(turtleShell);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addState(walk.title, turtleShell.title, "squashMe");
        turtle.addComponent(stateMachine);

        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.DYNAMIC);
        rigidBody2d.setMass(15.0f);
        rigidBody2d.setFixedRotation(true);
        turtle.addComponent(rigidBody2d);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.13f);
        circleCollider.setOffset(0.0f, -0.05f);
        turtle.addComponent(circleCollider);

        turtle.addComponent(new TurtleAI());

        return turtle;
    }

    public static GameObject generateFlagtop(Sprite sprite, float xSize, float ySize) {
        GameObject flagtop = generateFlag(6);
        flagtop.addComponent(new Flagpole(true));

        return flagtop;
    }

    public static GameObject generateFlagpole(Sprite sprite, float xSize, float ySize) {
        GameObject flagpole = generateFlag(33);
        flagpole.addComponent(new Flagpole(false));
        return flagpole;
    }

    private static GameObject generateFlag(int index) {
        GameObject flag = generateSpriteObject(AssetPool.getSpritesheet("assets/images/spritesheets/items.png")
                .getSprite(index), 0.25f, 0.25f);

        RigidBody2d rigidBody2d = new RigidBody2d();
        rigidBody2d.setBodyType(BodyTypes.DYNAMIC);
        rigidBody2d.setFixedRotation(true);
        rigidBody2d.setContinuousCollision(false);
        flag.addComponent(rigidBody2d);

        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(0.1f, 0.25f);
        boxCollider.setOffset(-0.075f, 0.0f);
        flag.addComponent(boxCollider);
        // flag.addComponent(new Flagpole(true));

        return flag;
    }

    @Override
    public void imgui() {
        Prefabs.addPrefabImGui(CustomPrefabs::generateMario,
                AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png").getSprite(0));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generateQuestionMarkBlock,
                AssetPool.getSpritesheet("assets/images/spritesheets/items.png").getSprite(0));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generateGoomba,
                AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png").getSprite(15));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generatePipeDown,
                AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png").getSprite(0));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generatePipeUp,
                AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png").getSprite(1));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generatePipeRight,
                AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png").getSprite(2));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generatePipeLeft,
                AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png").getSprite(3));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generateTurtle,
                AssetPool.getSpritesheet("assets/images/spritesheets/turtle.png").getSprite(0));

        Prefabs.addPrefabImGui(CustomPrefabs::generateFlagtop,
                AssetPool.getSpritesheet("assets/images/spritesheets/items.png").getSprite(6));
        Prefabs.sameLine();
        Prefabs.addPrefabImGui(CustomPrefabs::generateFlagpole,
                AssetPool.getSpritesheet("assets/images/spritesheets/items.png").getSprite(33));
    }
}
