package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import sirius.SiriusTheFox;
import sirius.animations.StateMachine;
import sirius.rendering.Camera;
import sirius.utils.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2d;
import physics2d.components.RigidBody2d;

public class TurtleAI extends Component {
    private transient boolean goingRight;
    private transient RigidBody2d rigidBody2d;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean dead;
    private transient boolean moving;
    private transient StateMachine stateMachine;
    private float movingDebounce = 0.32f;
    private boolean checkTurtleLayer = true;
    private boolean undergroundTurtle;
    private float destroyTime = 2.0f;

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (movingDebounce < 0 && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    (moving || !dead) && hitNormal.y < 0.58f) {
                playerController.hurt();

                if (!playerController.isDead())
                    contact.setEnabled(false);
            } else if (!playerController.isDead() && playerController.isHurtInvincible()) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        GoombaAI goomba = collidingObject.getComponent(GoombaAI.class);
        if (dead && moving && goomba != null) {
            goomba.stomp();
            contact.setEnabled(false);
            AssetPool.getSound("assets/sounds/kick.ogg").play();
        }

        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!dead && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    hitNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
                walkSpeed *= 3.0f;
            } else if (movingDebounce < 0 && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    (moving || !dead) && hitNormal.y < 0.58f) {
                playerController.hurt();

            } else if (!playerController.isDead() && !playerController.isHurtInvincible()) {
                if (dead && hitNormal.y > 0.58f) {
                    playerController.enemyBounce();
                    moving = !moving;
                    goingRight = hitNormal.x < 0;
                } else if (dead && !moving) {
                    moving = true;
                    goingRight = hitNormal.x < 0;
                    movingDebounce = 0.32f;
                }
            } else if (!playerController.isDead() && playerController.isHurtInvincible()) {
                contact.setEnabled(false);
            }
        } else if (Math.abs(hitNormal.y) < 0.1f && !collidingObject.isDead() && collidingObject.getComponent(MushroomAI.class) == null) {
            goingRight = hitNormal.x < 0;
            if (moving && dead) {
                AssetPool.getSound("assets/sounds/bump.ogg").play();
            }
        }

        if (collidingObject.getComponent(Fireball.class) != null) {
            if (!dead) {
                walkSpeed *= 3.0f;
                stomp();
            } else {
                moving = !moving;
                goingRight = hitNormal.x < 0;
            }
            collidingObject.getComponent(Fireball.class).disappear();
            contact.setEnabled(false);
        }
    }

    @Override
    public void start() {
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.rigidBody2d = this.gameObject.getComponent(RigidBody2d.class);
        this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt) {
        // Only remove if we are in the same layer as it is.
        if (SiriusTheFox.getCurrentScene().getGameObject("GameCamera")
                .getComponent(GameCamera.class).isUnderground() == undergroundTurtle) {

            if (this.gameObject.transform.position.x <
                    SiriusTheFox.getCurrentScene().getCamera().position.x - 0.5f) {
                destroyTime -= dt;

                if (destroyTime < 0)
                    this.gameObject.destroy();
            } else {
                destroyTime = 2.0f;
            }
        }

        if (checkTurtleLayer) {
            if (gameObject.transform.position.y < -SiriusTheFox.getCurrentScene()
                    .getGameObject("GameCamera").getComponent(GameCamera.class).getCameraBuffer()) {
                undergroundTurtle = true;
            }
            checkTurtleLayer = false;
        }

        movingDebounce -= dt;
        Camera camera = SiriusTheFox.getCurrentScene().getCamera();
        if (this.gameObject.transform.position.x >
                camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if (!dead || moving) {
            if (goingRight) {
                gameObject.transform.scale.x = -0.25f;
                velocity.x = walkSpeed;
                acceleration.x = 0;
            } else {
                gameObject.transform.scale.x = 0.25f;
                velocity.x = -walkSpeed;
                acceleration.x = 0;
            }
        } else {
            velocity.x = 0;
        }

        // TODO: 11/04/2022 Make PhysicsController to handle collisions
        if (isOnGround()) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
        }
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rigidBody2d.setVelocity(velocity);
    }

    private boolean isOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.2f;

        return Physics2d.isOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    private void stomp() {
        dead = true;
        moving = false;
        this.velocity.zero();
        rigidBody2d.setVelocity(velocity);
        rigidBody2d.setAngularVelocity(0.0f);
        rigidBody2d.setGravityScale(0.0f);
        stateMachine.trigger("squashMe");
        AssetPool.getSound("assets/sounds/bump.ogg").play();
    }
}
