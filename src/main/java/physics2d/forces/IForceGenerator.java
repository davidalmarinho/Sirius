package physics2d.forces;

import physics2d.rigidBody.RigidBody2D;

public interface IForceGenerator {
    void update(RigidBody2D rigidBody2D, float dt);
}
