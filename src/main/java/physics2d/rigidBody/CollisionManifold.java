package physics2d.rigidBody;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CollisionManifold {
    private boolean colliding;
    private Vector2f normal;
    private List<Vector2f> contactPoints;
    private float collisionDepth;

    public CollisionManifold() {
        this(new Vector2f(), 0.0f);
    }

    public CollisionManifold(Vector2f normal, float collisionDepth) {
        this.normal = normal;
        this.contactPoints = new ArrayList<>();
        this.collisionDepth = collisionDepth;
        this.colliding = true;
    }

    public Vector2f getNormal() {
        return normal;
    }

    public List<Vector2f> getContactPoints() {
        return contactPoints;
    }

    public void addContactPoint(Vector2f contactPoint) {
        this.contactPoints.add(contactPoint);
    }

    public float getCollisionDepth() {
        return collisionDepth;
    }
}
