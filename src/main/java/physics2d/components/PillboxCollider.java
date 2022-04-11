package physics2d.components;

import jade.SiriusTheFox;
import org.joml.Vector2f;

/**
 * PillboxCollider is a component that was created especially to platform games.
 * Basically it is 2 circle colliders that makes a pill inside the game object.
 */
public class PillboxCollider extends Collider2d {
    private transient CircleCollider circle;
    private transient Box2DCollider boxCollider;

    // Flag to reset the size of the hit box according our needs.
    private transient boolean resetFixtureNextFrame = false;

    private float width = 0.1f;
    private float height = 0.2f;

    public PillboxCollider() {
        this.circle = new CircleCollider();
        this.boxCollider  = new Box2DCollider();
        this.offset = new Vector2f();
    }

    @Override
    public void start() {
        // Assign our references --We will be doing a combo of colliders, and we want that our game object
        // be the pretended game object.
        this.circle.gameObject       = this.gameObject;
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
        circle.editorUpdate(dt);
        boxCollider.editorUpdate(dt);
        recalculateColliders();

        if (resetFixtureNextFrame) resetFixture();
    }

    private void recalculateColliders() {
        float circleRadius = width / 2.0f;
        float boxHeight = height - circleRadius;

        // Set circles' on box's edges
        circle.setRadius(circleRadius);
        circle.setOffset(new Vector2f(offset).sub(0, (height - circleRadius * 2.0f) / 2.0f));
        boxCollider.setHalfSize(new Vector2f(width - 0.01f, boxHeight));
        boxCollider.setOffset(new Vector2f(offset).add(0, (height - boxHeight) / 2.0f));
    }

    /**
     * Resets the fixtures
     */
    private void resetFixture() {
        if (SiriusTheFox.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            RigidBody2d rigidBody = gameObject.getComponent(RigidBody2d.class);
            if (rigidBody != null) SiriusTheFox.getPhysics().resetPillboxCollider(rigidBody, this);
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

    public CircleCollider getCircle() {
        return circle;
    }

    public Box2DCollider getBoxCollider() {
        return boxCollider;
    }
}
