package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.SiriusTheFox;
import jade.animations.StateMachine;
import jade.rendering.Camera;
import jade.utils.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2d;
import physics2d.components.RigidBody2d;

public class GoombaAI extends Component {
    private transient boolean goingRight;
    private transient RigidBody2d rigidBody2d;
    private float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f accleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f();
    private transient float timeToKill = 0.5f;
    private transient StateMachine stateMachine;
    private boolean dead;

    private void stomp() {
        this.dead = true;
        this.velocity.zero();
        this.rigidBody2d.setVelocity(new Vector2f());
        this.rigidBody2d.setAngularVelocity(0.0f);
        this.rigidBody2d.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        this.rigidBody2d.setSensor(true);
        AssetPool.getSound("assets/sounds/bump.ogg").play();
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (dead) return;

        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!playerController.isDead() && !playerController.isHurtInvincible() && isPlayerOnTopGoomba(hitNormal)) {
                playerController.enemyBounce();
                stomp();
            } else if (!playerController.isDead() && !playerController.isHurtInvincible()) {
                playerController.hurt();
            }
        } else if (Math.abs(hitNormal.y) < 0.1f) {
            goingRight = hitNormal.x < 0;
        }
    }

    @Override
    public void start() {
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rigidBody2d = gameObject.getComponent(RigidBody2d.class);
        this.accleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt) {
        // Outside camera's edges, we will not update the Goomba
        Camera camera = SiriusTheFox.getCurrentScene().getCamera();
        if (this.gameObject.transform.position.x > camera.position.x + camera.getProjectionSize().x * camera.getZoom())
            return;

        if (dead) {
            timeToKill -= dt;
            if (timeToKill <= 0)
                this.gameObject.destroy();

            this.rigidBody2d.setVelocity(new Vector2f());
            return;
        }

        if (goingRight)
            velocity.x = walkSpeed;
        else
            velocity.x = -walkSpeed;

        if (isOnGround()) {
            this.accleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.accleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.accleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rigidBody2d.setVelocity(velocity);
    }

    private boolean isPlayerOnTopGoomba(Vector2f hitNormal) {
        return hitNormal.y > 0.58f;
    }

    public boolean isOnGround() {
        float innerGoombaWidth = 0.25f * 0.7f;
        float yVal = -0.14f;

        return Physics2d.isOnGround(this.gameObject, innerGoombaWidth, yVal);
    }
}
