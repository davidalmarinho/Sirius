package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import sirius.SiriusTheFox;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2d;
import physics2d.components.RigidBody2d;

public class Fireball extends Component {
    private static int fireballCount = 0;
    public transient boolean goingRight = false;

    private transient float fireballSpeed = 1.7f;
    private transient RigidBody2d rigidBody2d;
    private transient Vector2f velocity;
    private transient Vector2f acceleration;
    private transient Vector2f terminalVelocity;
    private transient float fireballLifeTime = 4.0f;

    public Fireball() {
        this.velocity = new Vector2f();
        this.acceleration = new Vector2f();
        this.terminalVelocity = new Vector2f(2.1f, 3.1f);
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (Math.abs(hitNormal.x) > 0.8f)
            this.goingRight = hitNormal.x < 0;
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (collidingObject.hasComponent(PlayerController.class) || collidingObject.hasComponent(Fireball.class))
            contact.setEnabled(false);
    }

    public void disappear() {
        fireballCount--;
        this.gameObject.destroy();
    }

    @Override
    public void start() {
        this.rigidBody2d = gameObject.getComponent(RigidBody2d.class);
        this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
        fireballCount++;
    }

    @Override
    public void update(float dt) {
        fireballLifeTime -= dt;
        if (fireballLifeTime <= 0) {
            disappear();
            return;
        }

        if (goingRight)
            velocity.x = fireballSpeed;
        else
            velocity.x = -fireballSpeed;

        if (isOnGround()) {
            this.acceleration.y = 1.5f;
            this.velocity.y = 2.5f;
        } else {
            this.acceleration.y = SiriusTheFox.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rigidBody2d.setVelocity(velocity);
    }

    private boolean isOnGround() {
        float innerGoombaWidth = 0.25f * 0.7f;
        float yVal = -0.09f;

        return Physics2d.isOnGround(this.gameObject, innerGoombaWidth, yVal);
    }

    public static boolean canSpawn() {
        return fireballCount < 4;
    }
}
