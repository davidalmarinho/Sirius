package physics2d_from_scratch.primitives;

import org.joml.Vector2f;
import physics2d_from_scratch.rigidBody.RigidBody2D;

public class Circle extends Collider2D {
    private float radius = 1.0f;
    private RigidBody2D rigidBody2D = null;

    /**
     * Gets the radius of the circle
     *
     * @return radius of the circle
     */
    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getCenter() {
        return rigidBody2D.getPosition();
    }

    public void setRigidBody2D(RigidBody2D rigidBody2D) {
        this.rigidBody2D = rigidBody2D;
    }

    @Override
    public float getInertialTensor(float mass) {
        return 0;
    }
}
