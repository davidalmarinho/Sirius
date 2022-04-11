package physics2d;

import gameobjects.GameObject;
import gameobjects.components.Transform;
import gameobjects.components.game_components.Ground;
import jade.SiriusTheFox;
import jade.rendering.Color;
import jade.rendering.debug.DebugDraw;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import physics2d.components.*;

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
        world.setContactListener(new ContactListener());
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

            bodyDef.angularDamping  = rigidBody2d.getAngularDamping();
            bodyDef.linearDamping   = rigidBody2d.getLinearDamping();
            bodyDef.fixedRotation   = rigidBody2d.isFixedRotation();
            bodyDef.bullet          = rigidBody2d.isContinuousCollision();
            bodyDef.gravityScale    = rigidBody2d.getGravityScale();
            bodyDef.angularVelocity = rigidBody2d.getAngularVelocity();
            bodyDef.userData        = rigidBody2d.gameObject;

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
            body.m_mass = rigidBody2d.getMass();
            rigidBody2d.setRawBody(body);
            CircleCollider circleCollider;
            Box2DCollider box2DCollider;
            PillboxCollider pillboxCollider;

            if ((circleCollider = gameObject.getComponent(CircleCollider.class)) != null)
                addCircleCollider(rigidBody2d, circleCollider);

            if ((box2DCollider = gameObject.getComponent(Box2DCollider.class)) != null)
                addBox2DCollider(rigidBody2d, box2DCollider);

            if ((pillboxCollider = gameObject.getComponent(PillboxCollider.class)) != null)
                addPillboxCollider(rigidBody2d, pillboxCollider);
        }
    }

    private void addCircleCollider(RigidBody2d rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null.";

        // Collision shape
        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();

        // Add all to box2D
        body.createFixture(fixtureDef);
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
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = box2DCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();

        // Add all to box2D
        body.createFixture(fixtureDef);
    }

    public void resetCircleCollider(RigidBody2d rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();

        // If there aren't any colliders, we will cancel this action
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, circleCollider);
        body.resetMassData();
    }

    private void resetBox2DCollider(RigidBody2d rb, Box2DCollider box2DCollider) {
        Body body = rb.getRawBody();

        // If there aren't any colliders, we will cancel this action
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rb, box2DCollider);
        body.resetMassData();
    }

    public void addPillboxCollider(RigidBody2d rb, PillboxCollider pillboxCollider) {
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null.";

         addBox2DCollider(rb, pillboxCollider.getBoxCollider());
         addCircleCollider(rb, pillboxCollider.getCircle());
    }

    public void resetPillboxCollider(RigidBody2d rb, PillboxCollider pillboxCollider) {
        Body body = rb.getRawBody();

        // If there aren't any colliders, we will cancel this action
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rb, pillboxCollider);
        body.resetMassData();
    }

    public RaycastInfo raycast(GameObject requestingObject, Vector2f start, Vector2f end) {
        RaycastInfo callback = new RaycastInfo(requestingObject);
        // Send a raycast to the World
        world.raycast(callback, new Vec2(start.x, start.y), new Vec2(end.x, end.y));
        return callback;
    }

    public void setSensor(RigidBody2d rigidBody2d, boolean sensor) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = sensor;
            fixture = fixture.m_next;
        }
    }

    /**
     * Gets the number of fixtures.
     *
     * @return the number of fixtures.
     */
    private int fixtureListSize(Body body) {
        int size = 0;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }

        return size;
    }

    public static boolean isOnGround(GameObject gameObject, float innerObjWidth, float height, boolean showRaycast) {
        Vector2f raycastBegin = new Vector2f(gameObject.transform.position);
        // float innerPlayerWidth = this.playerWith * 0.6f;

        // Get object's left foot
        raycastBegin.sub(innerObjWidth / 2.0f, 0.0f);

        // Raycast size according to object's height
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, height);
        RaycastInfo info = SiriusTheFox.getPhysics().raycast(gameObject, raycastBegin, raycastEnd);

        // Get object's right foot
        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerObjWidth, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(innerObjWidth, 0.0f);

        RaycastInfo info2 = SiriusTheFox.getPhysics().raycast(gameObject, raycast2Begin, raycast2End);

        if (showRaycast) {
            DebugDraw.addLine2D(raycastBegin, raycastEnd, new Color(1.0f, 0.0f, 0.0f));
            DebugDraw.addLine2D(raycast2Begin, raycast2End, new Color(1.0f, 0.0f, 0.0f));
        }

        return (info.hitSomething && info.hitObject != null && info.hitObject.hasComponent(Ground.class)
                && info2.hitSomething && info2.hitObject != null && info2.hitObject.hasComponent(Ground.class));
    }

    public static boolean isOnGround(GameObject gameObject, float innerObjWidth, float height) {
        return Physics2d.isOnGround(gameObject, innerObjWidth, height, false);
    }

    /**
     * Checks if we can touch the world's physics.
     *
     * @return true if we aren't allowed to change the Physics in the world.
     */
    public boolean isLocked() {
        return world.isLocked();
    }

    public Vector2f getGravity() {
        return new Vector2f(this.world.getGravity().x, this.world.getGravity().y);
    }
}
