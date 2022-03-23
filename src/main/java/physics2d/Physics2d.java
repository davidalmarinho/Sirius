package physics2d;

import gameobjects.GameObject;
import gameobjects.components.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2d;

public class Physics2d {
    private Vec2 gravity;
    private World world;

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;

    // Iterative Impulse Resolver -- Uses iterations to resolve a collision when it happens
    // The more we have, the more precise the physics engine will be, but slower too
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public Physics2d() {
        this.gravity = new Vec2(0, -10.0f);
        this.world = new World(gravity);
        // TODO: 27/02/2022 Test the code with this line of code and test it without this line of code
        // world.setSleepingAllowed(false);
    }

    /**
     * Updates Physics' code powered by JBox2D
     * @param dt Elapsed time per second
     */
    public void update(float dt) {
        physicsTime += dt;

        // Updates 60 times per second
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
         }
    }

    public void destroyGameObject(GameObject go) {
        RigidBody2d rigidBody2d = go.getComponent(RigidBody2d.class);
        if (rigidBody2d != null) {
            if (rigidBody2d.getRawBody() != null) {
                world.destroyBody(rigidBody2d.getRawBody());
                rigidBody2d.setRawBody(null);
            }
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
            bodyDef.userData       = rigidBody2d.gameObject;
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

            Body body = this.world.createBody(bodyDef);
            rigidBody2d.setRawBody(body);
            CircleCollider circleCollider;
            Box2DCollider box2DCollider;

            if ((circleCollider = gameObject.getComponent(CircleCollider.class)) != null) {
                // TODO: 23/03/2022 Add circle collider implementation too
                // shape.setRadius(circleCollider.getRadius());
            }

            if ((box2DCollider = gameObject.getComponent(Box2DCollider.class)) != null) {
                addBox2DCollider(rigidBody2d, box2DCollider);
            }
        }
    }

    private void addBox2DCollider(RigidBody2d rb, Box2DCollider box2DCollider) {
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null.";

        // Collision shape
        PolygonShape shape = new PolygonShape();

        // Gets the correct size for JBox2D -- correct for JBox2D lib, because it is theoretically wrong for me
        Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);

        Vector2f offset = box2DCollider.getOffset();
        Vector2f origin = new Vector2f(box2DCollider.getOrigin());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        // fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = box2DCollider.gameObject;
        // fixtureDef.isSensor = rb.isSensor();

        // Add all to box2D
        body.createFixture(fixtureDef);
    }
}
