package physics2d;

import org.joml.Vector2f;
import physics2d.forces.ForceRegistry;
import physics2d.forces.Gravity2D;
import physics2d.rigidBody.RigidBody2D;

import java.util.ArrayList;
import java.util.List;

// Brains behind all physics engine
public class PhysicsSystem2D {
    // Container of all the forces
    private ForceRegistry forceRegistry;

    private List<RigidBody2D> rigidBodyList;
    private Gravity2D gravity;
    private float fixedUpdate;

    public PhysicsSystem2D(float fixedUpdateDt, Vector2f gravity) {
        this.forceRegistry = new ForceRegistry();
        this.rigidBodyList = new ArrayList<>();
        this.gravity = new Gravity2D(gravity);
        this.fixedUpdate = fixedUpdateDt;
    }

    public void update(float dt) {
        // To ensure that the physics engine is running consistent
        // TODO: 08/02/2022 Make physics engine consistent
        fixedUpdate();
    }

    public void fixedUpdate() {
        forceRegistry.updateForces(fixedUpdate);

        // Update the  velocities of all RigidBodies
        for (int i = 0; i < rigidBodyList.size(); i++) {
            rigidBodyList.get(i).physicsUpdate(fixedUpdate);
        }
    }

    public void addRigidBody2D(RigidBody2D rigidBody2D) {
        this.rigidBodyList.add(rigidBody2D);

        // Register gravity
        this.forceRegistry.add(rigidBody2D, gravity);
    }
}
