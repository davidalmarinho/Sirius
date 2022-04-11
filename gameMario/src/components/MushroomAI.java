package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.utils.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2d;

public class MushroomAI extends Component {
    private transient boolean goingRight = true;
    private transient RigidBody2d rigidBody2d;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer;

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        if (gameObject.hasComponent(PlayerController.class)) {
            contact.setEnabled(false);
            if (!hitPlayer) {
                if (gameObject.getComponent(PlayerController.class).isSmall())
                    gameObject.getComponent(PlayerController.class).powerup();
                this.gameObject.destroy();
                hitPlayer = true;
            }
        }

        // if the contact was more horizontally than vertically
        if (Math.abs(contactNormal.y) < 0.1f)
            // Means we have been hit on the left side
            goingRight = contactNormal.x < 0;
    }

    @Override
    public void start() {
        this.rigidBody2d = gameObject.getComponent(RigidBody2d.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
    }

    @Override
    public void update(float dt) {
        if (goingRight && isNotTerminalVelocity())
            rigidBody2d.addVelocity(speed);
        else if (!goingRight && isNotTerminalVelocity())
            rigidBody2d.addVelocity(new Vector2f(-speed.x, speed.y)); // speed.y is theoretically 0
    }

    private boolean isNotTerminalVelocity() {
        return Math.abs(rigidBody2d.getVelocity().x) < maxSpeed;
    }
}
