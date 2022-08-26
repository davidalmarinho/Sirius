package physics2d.components;

import gameobjects.GameObject;
import sirius.SiriusTheFox;
import sirius.rendering.color.Color;
import sirius.rendering.debug.DebugDraw;
import org.joml.Vector2f;

public class CircleCollider extends Collider2d {
    private float radius = 1.0f;
    private transient boolean resetFixtureNextFrame = false;

    public CircleCollider() {
        this.offset = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        GameObject activeGameObject = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObject();
        if (activeGameObject == null || activeGameObject != gameObject) return;

        Vector2f center = new Vector2f(gameObject.getPosition()).add(getOffset());
        DebugDraw.addCircle(center, radius, Color.GREEN);

        if (resetFixtureNextFrame)
            resetFixture();
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) resetFixture();
    }

    private void resetFixture() {
        if (SiriusTheFox.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            RigidBody2d rigidBody2d = gameObject.getComponent(RigidBody2d.class);
            if (rigidBody2d != null) {
                SiriusTheFox.getPhysics().resetCircleCollider(rigidBody2d, this);
            }
        }
    }

    /**
     * Gets radius size
     * @return Radius' size
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets radius' size
     * @param radius The size that is wished
     */
    public void setRadius(float radius) {
        this.resetFixtureNextFrame = true;
        this.radius = radius;
    }
}
