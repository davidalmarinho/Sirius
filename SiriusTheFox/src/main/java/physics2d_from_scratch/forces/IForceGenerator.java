package physics2d_from_scratch.forces;

import physics2d_from_scratch.rigidBody.RigidBody2D;

public interface IForceGenerator {
    void update(RigidBody2D rigidBody2D, float dt);
}
