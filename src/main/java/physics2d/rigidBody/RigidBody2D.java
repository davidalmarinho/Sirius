package physics2d.rigidBody;

import gameobjects.components.Component;
import gameobjects.components.Transform;
import org.joml.Vector2f;
import physics2d.primitives.Collider2D;

public class RigidBody2D extends Component {
    // Transform attached to the object
    private Transform rawTransform;
    private Collider2D collider2D;

    private Vector2f position = new Vector2f();
    private float mass = 0.0f;
    private float inverseMass = 0.0f;
    private float rotation = 0.0f; // In degrees

    private Vector2f forceAccumulator = new Vector2f();
    private Vector2f linearVelocity = new Vector2f();
    private float angularVelocity;
    private float linearDamping;
    private float angularDamping;

    // Coefficient of restitution
    private float cor = 1.0f;

    private boolean fixedPosition;

    public void physicsUpdate(float dt) {
        // 0 kg objects doesn't exist (and we will be using 0 kg for non-moving game objects )
        if (this.mass == 0.0f) return;

        // -- Calculate linear velocity:
        // 2nd Newton's Law: F = m * a <=> a = F * (1 / mass)
        Vector2f acceleration = new Vector2f(forceAccumulator).mul(this.inverseMass);

        // a = (deltaV) / (deltaT) <=> deltaV = a * (deltaT)
        linearVelocity.add(acceleration.mul(dt));

        // Update the linear position
        // v = (deltaP) / (deltaT) <=> deltaP = v * deltaT
        this.position.add(new Vector2f(linearVelocity).mul(dt));

        syncCollisionTransform();
        clearAccumulators();
    }

    /**
     * Syncs the physics transform with the game object transform
     */
    public void syncCollisionTransform() {
        if (rawTransform != null) {
            rawTransform.position.set(this.position);
        }
    }

    /**
     * Clears the force accumulators
     */
    private void clearAccumulators() {
        this.forceAccumulator.zero();
    }

    public void addForce(Vector2f force) {
        this.forceAccumulator.add(force);
    }

    public boolean hasInfiniteMass() {
        return mass == 0.0f;
    }

    public void setTransform(Vector2f position, float rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void setTransform(Vector2f position) {
        this.position = position;
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }

    /**
     * Gets objects' position
     *
     * @return object's position
     */
    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(Vector2f linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public float getMass() {
        return mass;
    }

    /**
     * Sets mass and inverseMass variables
     * @param mass The object's mass
     */
    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != 0.0f) {
            this.inverseMass = 1.0f / this.mass;
        }
    }

    public float getInverseMass() {
        return inverseMass;
    }

    /**
     * Gets object's rotation in degrees
     *
     * @return object's rotation in degrees
     */
    public float getRotation() {
        return rotation;
    }

    public Collider2D getCollider() {
        return collider2D;
    }

    public void setCollider(Collider2D collider2D) {
        this.collider2D = collider2D;
    }

    public float getCor() {
        return cor;
    }

    public void setCor(float cor) {
        this.cor = cor;
    }
}
