package physics2d.primitives;

import org.joml.Vector2f;
import physics2d.rigidBody.RigidBody2D;

// Axis aligned Bounding Box
public class AABB {
    private Vector2f center;
    private Vector2f size, halfSize;
    private RigidBody2D rigidBody;

    public AABB(Vector2f bottomLeftCorner, Vector2f topRightCorner) {
        // this.size = new Vector2f(topRightCorner.x - bottomLeftCorner.x, topRightCorner.y - bottomLeftCorner.y);
        this.size = new Vector2f(topRightCorner).sub(bottomLeftCorner);
        this.halfSize = new Vector2f(size.mul(0.5f));
    }

    public Vector2f getBottomLeftCorner() {
        return new Vector2f(this.rigidBody.getPosition().sub(this.halfSize));
    }

    public Vector2f getTopRightCorner() {
        return new Vector2f(this.rigidBody.getPosition().add(this.halfSize));
    }
}
