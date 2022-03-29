package physics2d.components;

import gameobjects.components.Component;
import jade.Window;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;
import org.joml.Vector2f;

/**
 * PillboxCollider is a component that was created especially to platform games.
 * Basically it is 2 circle colliders that makes a pill inside the game object.
 */
public class PillboxCollider extends Collider2d {
    private transient CircleCollider topCircle, bottomCircle;
    private transient Box2DCollider boxCollider;

    // Flag to reset the size of the hit box according our needs.
    private transient boolean resetFixtureNextFrame;

    private float width = 0.1f;
    private float height = 0.2f;

    public PillboxCollider() {
        this.topCircle    = new CircleCollider();
        this.bottomCircle = new CircleCollider();
        this.boxCollider  = new Box2DCollider();
    }

    @Override
    public void start() {
        // Assign our references --We will be doing a combo of colliders, and we want that our game object
        // be the pretended game object.
        this.topCircle.gameObject    = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.boxCollider.gameObject  = this.gameObject;

        recalculateColliders();
    }

    @Override
    public void update(float dt) {
        // Try to reset fixture when the world allow us
        if (resetFixtureNextFrame)
            resetFixture();
    }

    @Override
    public void editorUpdate(float dt) {
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);

        if (resetFixtureNextFrame) resetFixture();
    }

    private void recalculateColliders() {
        float circleRadius = width / 4.0f;
        float boxHeight = height - 2 * circleRadius;

        // Set circles' on box's edges
        topCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(getOffset()).add(0, boxHeight / 4.0f));
        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2f(getOffset()).sub(0, boxHeight / 4.0f));

        boxCollider.setHalfSize(new Vector2f(width / 2.0f, boxHeight));
        boxCollider.setOffset(getOffset());
    }

    /**
     * Resets the fixtures
     */
    private void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            RigidBody2d rigidBody = gameObject.getComponent(RigidBody2d.class);
            if (rigidBody != null) Window.getPhysics().resetPillboxCollider(rigidBody, this);
        }
    }

    public void setWidth(float width) {
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        recalculateColliders();
        resetFixture();
    }

    public void setSize(float width, float height) {
        this.width  = width;
        this.height = height;
        recalculateColliders();
        resetFixture();
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBoxCollider() {
        return boxCollider;
    }
}
