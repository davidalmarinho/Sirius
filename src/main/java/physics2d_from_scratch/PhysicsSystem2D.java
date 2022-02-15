package physics2d_from_scratch;

import org.joml.Vector2f;
import physics2d_from_scratch.forces.ForceRegistry;
import physics2d_from_scratch.forces.Gravity2D;
import physics2d_from_scratch.primitives.Collider2D;
import physics2d_from_scratch.rigidBody.CollisionManifold;
import physics2d_from_scratch.rigidBody.Collisions;
import physics2d_from_scratch.rigidBody.RigidBody2D;

import java.util.ArrayList;
import java.util.List;

// Brains behind all physics engine
public class PhysicsSystem2D {
    // Container of all the forces
    private ForceRegistry forceRegistry;
    private Gravity2D gravity;

    private List<RigidBody2D> rigidBodyList;
    private List<RigidBody2D> body1List;
    private List<RigidBody2D> body2List;
    private List<CollisionManifold> collisionList;

    private float fixedUpdate;
    private int impulseIterations = 6;

    public PhysicsSystem2D(float fixedUpdateDt, Vector2f gravity) {
        this.forceRegistry = new ForceRegistry();
        this.gravity = new Gravity2D(gravity);

        this.rigidBodyList = new ArrayList<>();
        this.body1List = new ArrayList<>();
        this.body2List = new ArrayList<>();
        this.collisionList = new ArrayList<>();

        this.fixedUpdate = fixedUpdateDt;
    }

    public void update(float dt) {
        // To ensure that the physics engine is running consistent
        fixedUpdate();
    }

    public void fixedUpdate() {
        body1List.clear();
        body2List.clear();
        collisionList.clear();

        // Find any collisions
        int size = rigidBodyList.size();
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                // We don't want to check the collision between 2 game objects
                if (i == j) continue;

                CollisionManifold result = new CollisionManifold();
                RigidBody2D r1 = rigidBodyList.get(i);
                RigidBody2D r2 = rigidBodyList.get(j);
                Collider2D c1 = r1.getCollider();
                Collider2D c2 = r2.getCollider();

                // Just keep going if colliders exist and if objects doesn't have infinite mass
                if (c1 != null && c2 != null && !(r1.hasInfiniteMass() && r2.hasInfiniteMass())) {
                    result = Collisions.findCollisionFeatures(c1, c2);
                }

                if (result != null && result.isColliding()) {
                    body1List.add(r1);
                    body2List.add(r2);
                    collisionList.add(result);
                }
            }
        }

        // Update the forces
        forceRegistry.updateForces(fixedUpdate);

        // Resolve collisions via iterative impulse resolution
        // iterate a certain amount of times to get an approximate solution
        for (int k = 0; k < impulseIterations; k++) {
            for (int i = 0; i < collisionList.size(); i++) {
                int jSize = collisionList.get(i).getContactPoints().size();
                for (int j = 0; j < jSize; j++) {
                    RigidBody2D r1 = body1List.get(i);
                    RigidBody2D r2 = body2List.get(i);
                    applyImpulse(r1, r2, collisionList.get(i));
                }
            }
        }

        // Update the  velocities of all RigidBodies
        for (int i = 0; i < rigidBodyList.size(); i++) {
            rigidBodyList.get(i).physicsUpdate(fixedUpdate);
        }

        // TODO: 09/02/2022 Apply linear projection because of bugs
    }

    private void applyImpulse(RigidBody2D a, RigidBody2D b, CollisionManifold collisionManifold) {
        // Linear velocity
        float inverseMass1 = a.getInverseMass();
        float inverseMass2 = b.getInverseMass();
        float inverseMassSum = inverseMass1 + inverseMass2;

        // Don't want infinite masses
        if (inverseMassSum == 0.0f) return;

        // Relative velocity
        Vector2f relativeVelocity = new Vector2f(b.getLinearVelocity()).sub(a.getLinearVelocity());
        Vector2f relativeNormal = new Vector2f(collisionManifold.getNormal()).normalize();

        // If they are moving away of each other, we don't need to resolve the collision
        if (relativeVelocity.dot(relativeNormal) > 0.0f) return;

        float e = Math.min(a.getCor(), b.getCor());

        float numerator = (-(1.0f + e) * relativeVelocity.dot(relativeNormal));
        float j = numerator / inverseMassSum;

        // Distribute impulse throw contact points
        if (collisionManifold.getContactPoints().size() > 0 && j != 0.0f)
            j /= (float) collisionManifold.getContactPoints().size();

        // Impulse in direction of collision normal
        Vector2f impulse = new Vector2f(relativeNormal).mul(j);
        a.setLinearVelocity(
                new Vector2f(a.getLinearVelocity()).add(new Vector2f(impulse).mul(inverseMass1).mul(-1f)));
        b.setLinearVelocity(
                new Vector2f(b.getLinearVelocity()).add(new Vector2f(impulse).mul(inverseMass2).mul(1f)));
    }

    public void addRigidBody2D(RigidBody2D rigidBody2D, boolean addGravity) {
        this.rigidBodyList.add(rigidBody2D);

        // Register gravity
        if (addGravity) this.forceRegistry.add(rigidBody2D, gravity);
    }
}
