package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import gameobjects.components.game_components.Ground;
import sirius.SiriusTheFox;
import sirius.animations.StateMachine;
import sirius.input.KeyListener;
import sirius.rendering.Color;
import sirius.scenes.ISceneInitializer;
import sirius.scenes.LevelSceneInitializer;
import sirius.utils.AssetPool;
import sirius.utils.Settings;
import main.CustomPrefabs;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import physics2d.BodyTypes;
import physics2d.Physics2d;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2d;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class PlayerController extends Component {

    private enum PlayerState {
        SMALL,
        BIG,
        FIRE,
        INVINCIBLE,
        DEAD
    }

    public float walkSpeed           = 1.9f;
    public float jumpBoost           = 1.0f;
    public float jumpImpulse         = 3.0f;
    public float slowDownForce       = 0.5f; // Like friction
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private PlayerState playerState = PlayerState.SMALL;

    // Basically, when the player isn't in the ground anymore, you still have some time to still jump
    public transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;

    private transient RigidBody2d rigidBody2d;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWith = Settings.GRID_HEIGHT;
    private transient int jumpTime;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean dead;
    private transient int enemyBounce = 0;
    private transient float hurtInvincibilityTimeLeft;
    private transient float hurtInvincibilityTime = 1.4f;
    private transient float deadMaxHeight;
    private transient float deadMinHeight;
    private transient boolean deadGoingUp = true;
    private transient float blinkTime = 0.0f;
    private transient SpriteRenderer spriteRenderer;

    private boolean win;
    private transient float timeToCastle = 4.5f;
    private transient float walkTime = 2.2f;
    private transient boolean sentEvent;


    @Override
    public void start() {
        this.spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        this.rigidBody2d    = gameObject.getComponent(RigidBody2d.class);
        this.stateMachine   = gameObject.getComponent(StateMachine.class);

        // Don't want to the Physics to control the Player. We will control the velocity and that stuff by ourselves
        this.rigidBody2d.setGravityScale(0.0f);
    }

    private void changeScene() {
        ISceneInitializer customSceneInitializer = SiriusTheFox.getCustomSceneInitializer();
        if (customSceneInitializer != null)
            SiriusTheFox.changeScene(customSceneInitializer.build());
        else
            SiriusTheFox.changeScene(new LevelSceneInitializer());
    }

    public void powerup() {
        if (playerState == PlayerState.SMALL) {
            playerState = PlayerState.BIG;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null) {
                // Turn the forces stronger
                jumpBoost *= bigJumpBoostFactor;
                walkSpeed *= bigJumpBoostFactor;
                pb.setHeight(0.42f);
            }
        } else if (playerState == PlayerState.BIG) {
            playerState = PlayerState.FIRE;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
        }

        stateMachine.trigger("powerup");
    }

    @Override
    public void beginCollision(GameObject collidingGameObject, Contact contact, Vector2f contactNormal) {
        if (dead) return;

        if (collidingGameObject.hasComponent(Ground.class)) {
            // Checks if we hit horizontally
            if (Math.abs(contactNormal.x) > 0.8f) {
                this.velocity.x = 0;

            // Checks if we hit vertically
            } else if (contactNormal.y > 0.8f) {
                this.velocity.y     = 0;
                this.acceleration.y = 0;
                this.jumpTime       = 0;
            }
        }
    }

    private boolean isOnGround() {
        float innerPlayerWidth = this.playerWith * 0.6f;
        float yVal = playerState == PlayerState.SMALL ? -0.14f : -0.24f;

        return Physics2d.isOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    private void shootFireballs() {
        if (KeyListener.isKeyDown(GLFW_KEY_E) && playerState == PlayerState.FIRE && Fireball.canSpawn()) {
            Vector2f position = new Vector2f(this.gameObject.transform.position)
                    .add(this.gameObject.transform.scale.x > 0
                            ? new Vector2f(0.26f, 0.0f) : new Vector2f(-0.26f, 0.0f));
            GameObject fireball = CustomPrefabs.generateFireball(position);
            fireball.getComponent(Fireball.class).goingRight = this.gameObject.transform.scale.x > 0;
            AssetPool.getSound("assets/sounds/fireball.ogg").play();
            SiriusTheFox.getCurrentScene().addGameObject(fireball);
        }
    }

    @Override
    public void update(float dt) {
        if (!dead && !hasWon())
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
        else if (dead || hasWon())
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();

        if (hasWon()) {
            if (!isOnGround()) {
                gameObject.transform.scale.x = -0.25f;
                gameObject.transform.position.y -= dt;
                stateMachine.trigger("stopRunning");
                stateMachine.trigger("stopJumping");
            } else {
                if (this.walkTime > 0) {
                    gameObject.transform.scale.x = 0.25f;
                    gameObject.transform.position.x += dt;
                    stateMachine.trigger("startRunning");
                }
                if (!AssetPool.getSound("assets/sounds/stage_clear.ogg").isPlaying())
                    AssetPool.getSound("assets/sounds/stage_clear.ogg").play();

                timeToCastle -= dt;
                walkTime -= dt;

                if (timeToCastle <= 0)
                    changeScene();
            }
            return;
        }

        // Death animation
        if (dead) {
            if (this.gameObject.transform.position.y < deadMaxHeight && deadGoingUp) {
                this.gameObject.transform.position.y += dt * walkSpeed / 2.0f;
            } else if (this.gameObject.transform.position.y >= deadMaxHeight && deadGoingUp) {
                deadGoingUp = false;
            } else  if (!deadGoingUp && gameObject.transform.position.y > deadMinHeight) {
                this.rigidBody2d.setBodyType(BodyTypes.KINEMATIC);
                this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
                this.velocity.y += this.acceleration.y * dt;
                this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
                this.rigidBody2d.setVelocity(this.velocity);
                this.rigidBody2d.setAngularVelocity(0);

                // If we are outside of screen's edges
            } else if (!deadGoingUp && gameObject.transform.position.y <= deadMinHeight) {
                changeScene();
            }

            return;
        }

        if (hurtInvincibilityTimeLeft > 0) {
            hurtInvincibilityTimeLeft -= dt;
            blinkTime -= dt;

            if (blinkTime <= 0) {
                blinkTime = 0.2f;
                if (spriteRenderer.getColor().w == 1)
                    spriteRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
                else
                    spriteRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            } else {
                if (spriteRenderer.getColor().w == 0)
                    spriteRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            }
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || isOnGround() || groundDebounce > 0)) {
            if (jumpTime == 0 && (isOnGround() || groundDebounce > 0)) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                jumpTime = 28;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = ((jumpTime / 2.02f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }

            groundDebounce = 0;
        } else if (enemyBounce > 0) {
            enemyBounce--;
            this.velocity.y = (enemyBounce / 2.2f) * jumpBoost;

        } else if (!isOnGround()) {
            // If we are in the middle of a jump and if we release space key
            if (this.jumpTime > 0) {
                this.velocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= dt;
            this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;

        // If we are in the ground
        } else {
            this.velocity.y = 0.0f;
            this.acceleration.y = 0.0f;
            groundDebounce = groundDebounceTime;
        }

        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
            this.acceleration.x = walkSpeed;

            // Changes player's direction when he switches direction
            this.gameObject.transform.scale.x = playerWith;
            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {
            this.acceleration.x = -walkSpeed;

            // Changes player's direction when he switches direction
            this.gameObject.transform.scale.x = -playerWith;
            if (this.velocity.x > 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else {
            this.acceleration.x = 0;

            // If going to the right
            if (this.velocity.x > 0)
                this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
            // If going to the left
            else if (this.velocity.x < 0)
                this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);

            if (this.velocity.x == 0) {
                this.stateMachine.trigger("stopRunning");
            }
        }

        this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;

        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rigidBody2d.setVelocity(this.velocity);
        this.rigidBody2d.setAngularVelocity(0);

        if (!isOnGround())
            stateMachine.trigger("jump");
        else
            stateMachine.trigger("stopJumping");

        shootFireballs();
    }

    public void hurt() {
        this.stateMachine.trigger("die");
        switch (playerState) {
            case SMALL:
                this.playerState = PlayerState.DEAD;
                this.velocity.set(0, 0);
                this.acceleration.set(0, 0);
                this.rigidBody2d.setVelocity(new Vector2f());
                this.dead = true;
                this.rigidBody2d.setSensor(true);
                AssetPool.getSound("assets/sounds/mario_die.ogg").play();
                deadMaxHeight = this.gameObject.transform.position.y + 0.3f;
                this.rigidBody2d.setBodyType(BodyTypes.STATIC);
                if (gameObject.transform.position.y > 0) deadMinHeight = -0.25f;
                break;

            case BIG:
                this.playerState = PlayerState.SMALL;
                gameObject.transform.scale.y = 0.25f;
                PillboxCollider pillboxCollider = gameObject.getComponent(PillboxCollider.class);

                // We are small, so the jumps have to be smaller too
                if (pillboxCollider != null) {
                    jumpBoost /= bigJumpBoostFactor;
                    walkSpeed /= bigJumpBoostFactor;
                    pillboxCollider.setHeight(0.25f);
                }

                hurtInvincibilityTimeLeft = hurtInvincibilityTime;
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                break;

            case FIRE:
                this.playerState = PlayerState.BIG;
                hurtInvincibilityTimeLeft = hurtInvincibilityTime;
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                break;
        }
    }

    public void playWinAnimation(GameObject flagpole) {
        if (!hasWon()) {
            win = true;
            velocity.set(0.0f, 0.0f);
            acceleration.set(0.0f, 0.0f);
            rigidBody2d.setVelocity(velocity);
            rigidBody2d.setSensor(true);
            rigidBody2d.setBodyType(BodyTypes.STATIC);
            gameObject.transform.position.x = flagpole.transform.position.x;
            AssetPool.getSound("assets/sounds/flagpole.ogg").play();
        }
    }

    public void setPosition(Vector2f newPosition) {
        this.gameObject.transform.position.set(newPosition);
        this.rigidBody2d.setPosition(newPosition);
    }

    public void enemyBounce() {
        this.enemyBounce = 8;
    }

    public boolean isSmall() {
        return playerState == PlayerState.SMALL;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isHurtInvincible() {
        return this.hurtInvincibilityTimeLeft > 0 || hasWon();
    }

    public boolean isInvincible() {
        return this.playerState == PlayerState.INVINCIBLE || isHurtInvincible();
    }

    public boolean hasWon() {
        return win;
    }
}
