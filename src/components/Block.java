package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import sirius.utils.Pool;

public abstract class Block extends Component {
    private transient boolean bopGoingUp = true;
    private transient boolean doBopAnimation;
    private transient Vector2f bopStart;
    private transient Vector2f topBopLocation;
    private transient boolean active = true;

    public float bopSpeed = 0.4f;

    abstract void playerHit(PlayerController playerController);

    @Override
    public void start() {
        this.bopStart = new Vector2f(this.gameObject.getPosition());
        this.topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    @Override
    public void update(float dt) {
        if (doBopAnimation) {
            if (bopGoingUp) {
                if (this.gameObject.getPosition().y < topBopLocation.y)
                    this.gameObject.transform(0, bopSpeed * dt);
                else
                    bopGoingUp = false;
            } else {
                if (this.gameObject.getPosition().y > bopStart.y)
                    this.gameObject.transform(0, -bopSpeed * dt);
                else {
                    this.gameObject.setPosition(gameObject.getPosition().x, this.bopStart.y);
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = gameObject.getComponent(PlayerController.class);

        if (active && playerController != null && contactNormal.y < -0.8f) {
            doBopAnimation = true;
            Pool.Assets.getSound("assets/sounds/bump.ogg").play();
            playerHit(playerController);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
