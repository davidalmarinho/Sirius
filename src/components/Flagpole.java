package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Flagpole extends Component {
    private boolean toppole;

    public Flagpole(boolean toppole) {
        this.toppole = toppole;
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController == null) return;

        playerController.playWinAnimation(this.gameObject);
    }
}
