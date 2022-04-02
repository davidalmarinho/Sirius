package gameobjects.components;

import jade.Window;
import jade.animations.StateMachine;
import jade.input.KeyListener;
import jade.utils.Settings;
import org.joml.Vector2f;
import physics2d.components.RigidBody2d;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public float walkSpeed           = 1.9f;
    public float jumpBoost           = 1.0f;
    public float jumpImpulse         = 3.0f;
    public float slowDownForce       = 0.5f; // Like friction
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    public transient boolean onGround;

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

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            this.acceleration.x = walkSpeed;

            // Changes player's direction when he switches direction
            this.gameObject.transform.scale.x = playerWith;
            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
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

        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;

        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rigidBody2d.setVelocity(this.velocity);
        this.rigidBody2d.setAngularVelocity(0);
    }
}
