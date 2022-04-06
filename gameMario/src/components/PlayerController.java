package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.SiriusTheFox;
import jade.animations.StateMachine;
import jade.input.KeyListener;
import jade.rendering.Color;
import jade.rendering.debug.DebugDraw;
import jade.utils.AssetPool;
import jade.utils.Settings;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import physics2d.components.PillboxCollider;
import physics2d.components.RaycastInfo;
import physics2d.components.RigidBody2d;

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

    @Override
    public void start() {
        this.rigidBody2d = gameObject.getComponent(RigidBody2d.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);

        // Don't want to the Physics to control the Player. We will control the velocity and that stuff by ourselves
        this.rigidBody2d.setGravityScale(0.0f);
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
                pb.setHeight(0.63f);
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
        Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
        float innerPlayerWidth = this.playerWith * 0.6f;

        // Get mario's left foot
        raycastBegin.sub(innerPlayerWidth / 2.0f, 0.0f);

        // Raycast size according to mario's height
        float yVal = playerState == PlayerState.SMALL ? -0.14f : -0.24f;

        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, yVal);
        RaycastInfo info = SiriusTheFox.getPhysics().raycast(gameObject, raycastBegin, raycastEnd);

        // Get mario's right foot
        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0.0f);

        RaycastInfo info2 = SiriusTheFox.getPhysics().raycast(gameObject, raycast2Begin, raycast2End);

        DebugDraw.addLine2D(raycastBegin, raycastEnd, new Color(1.0f, 0.0f, 0.0f));
        DebugDraw.addLine2D(raycast2Begin, raycast2End, new Color(1.0f, 0.0f, 0.0f));

        return (info.hitSomething && info.hitObject != null && info.hitObject.hasComponent(Ground.class)
                && info2.hitSomething && info2.hitObject != null && info2.hitObject.hasComponent(Ground.class));
    }

    @Override
    public void update(float dt) {
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
    }

    public boolean isSmall() {
        return playerState == PlayerState.SMALL;
    }
}
