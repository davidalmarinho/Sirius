package physics2d.rigidBody;

import org.joml.Vector2f;
import physics2d.primitives.Circle;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Circle a, Circle b) {
        CollisionManifold result = new CollisionManifold();
        float sumRadius = a.getRadius() + b.getRadius();
        Vector2f distanceVec = new Vector2f(b.getCenter()).sub(a.getCenter());

        // If we aren't colliding -- because the distance between the 2 circles can't be less than the sum of its radius to a collision exist
        if (distanceVec.lengthSquared() - sumRadius * sumRadius > 0) {
            return result;
        }

        // Get collisionDepth -- Multiply by 0.5 because we want to seperate each circle the same amount.
        // Consider updating to factor in the momentum and velocity
        float depth = Math.abs(distanceVec.length() - sumRadius) * 0.5f;
        Vector2f normal = new Vector2f(distanceVec);
        normal.normalize();

        float distanceToIntersectionPoint = a.getRadius() - depth;
        Vector2f contactPoint = new Vector2f(a.getCenter()).add(new Vector2f(normal).mul(distanceToIntersectionPoint));

        result = new CollisionManifold(normal, depth);
        result.addContactPoint(contactPoint);
        return result;
    }
}
