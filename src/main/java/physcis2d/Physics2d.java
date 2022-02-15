package physcis2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Physics2d {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;

    // Iterative Impulse Resolver -- Uses iterations to resolve a collision when it happens
    // The more we have, the more precise the physics engine will be, but slower too
    private int velocityIterations = 8;
    private int positionIterations = 3;

    /**
     * Updates Physics' code powered by JBox2D
     * @param dt Elapsed time per second
     */
    public void update(float dt) {
        physicsTime += dt;

        // Updates 60 times per second
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTime, velocityIterations, positionIterations);
        }
    }
}
