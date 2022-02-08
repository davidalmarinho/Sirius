package physics2d.forces;

import org.joml.Vector2f;
import physics2d.rigidBody.RigidBody2D;

public class Gravity2D implements IForceGenerator {

    private Vector2f gravity;

    /**
     * Gravity2D constructor method
     * @param force Amount of gravity that our game world will have
     */
    public Gravity2D(Vector2f force) {
        this.gravity = new Vector2f(force);
    }

    @Override
    public void update(RigidBody2D rigidBody2D, float dt) {
        // F = m * a
        rigidBody2D.addForce(new Vector2f(gravity).mul(rigidBody2D.getMass()));
    }
}
