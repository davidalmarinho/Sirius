package physics2d;

import gameobjects.GameObject;
import gameobjects.components.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2d;

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

    public void destroyGameObject(GameObject go) {
        RigidBody2d rigidBody2d = go.getComponent(RigidBody2d.class);
        if (rigidBody2d != null) {
            world.destroyBody(rigidBody2d.getRawBody());
            rigidBody2d.setRawBody(null);
        }
    }

    public void add(GameObject gameObject) {
        RigidBody2d rigidBody2d = gameObject.getComponent(RigidBody2d.class);
        if (rigidBody2d != null && rigidBody2d.getRawBody() == null) {
            Transform transform = gameObject.transform;

            // Tell how to create the body
            BodyDef bodyDef = new BodyDef();
            bodyDef.angle   = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);

            bodyDef.angularDamping = rigidBody2d.getAngularDamping();
            bodyDef.linearDamping  = rigidBody2d.getLinearDamping();
            bodyDef.fixedRotation  = rigidBody2d.isFixedRotation();
            bodyDef.bullet         = rigidBody2d.isContinuousCollision();

            switch (rigidBody2d.getEBodyType()) {
                case KINEMATIC:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
                case STATIC:
                    bodyDef.type = BodyType.STATIC;
                    break;
                case DYNAMIC:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
            }

            // Collision shapes
            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2DCollider box2DCollider;
            if ((circleCollider = gameObject.getComponent(CircleCollider.class)) != null) {
                shape.setRadius(circleCollider.getRadius());
            } else if ((box2DCollider = gameObject.getComponent(Box2DCollider.class)) != null) {
                // Gets the correct size for JBox2D -- correct for JBox2D lib, because it is theoretically wrong for me
                Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);

                Vector2f offset = box2DCollider.getOffset();
                Vector2f origin = new Vector2f(box2DCollider.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            // Add all to box 2d engine
            Body body = this.world.createBody(bodyDef);
            rigidBody2d.setRawBody(body);
            body.createFixture(shape, rigidBody2d.getMass());
        }
    }
}
