package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.utils.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2d;

public class Flower extends Component {
    private transient RigidBody2d rigidBody2d;

    @Override
    public void start() {
        this.rigidBody2d = gameObject.getComponent(RigidBody2d.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        this.rigidBody2d.setSensor(true);
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if (playerController != null) {
            playerController.powerup();
            this.gameObject.destroy();
        }
    }
}
