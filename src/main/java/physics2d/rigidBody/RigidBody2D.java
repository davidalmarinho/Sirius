package physics2d.rigidBody;

import gameobjects.components.Component;
import org.joml.Vector2f;

public class RigidBody2D extends Component {
    private Vector2f position = new Vector2f();
    private float rotation = 0.0f; // In degrees

    /**
     * Gets objects' position
     *
     * @return object's position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Changes object's position
     *
     * @param position object's new position
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }

    /**
     * Gets object's rotation in degrees
     *
     * @return object's rotation in degrees
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Rotates object
     *
     * @param rotation how much need to rotate in degrees
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
