package physics2d.rigidBody;

import gameobjects.components.Component;
import org.joml.Vector2f;

public class RigidBody2D extends Component {
    private Vector2f position = new Vector2f();
    private float rotation = 0.0f; // In degrees

    private Vector2f linearVelocity = new Vector2f();
    private float angularVelocity;
    private float linearDamping;
    private float angularDamping;

    private boolean fixedPosition;

    public void setTransform(Vector2f position, float rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void setTransform(Vector2f position) {
        this.position = position;
    }

    /**
     * Gets objects' position
     *
     * @return object's position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Gets object's rotation in degrees
     *
     * @return object's rotation in degrees
     */
    public float getRotation() {
        return rotation;
    }
}
