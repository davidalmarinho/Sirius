package physics2d.components;

import gameobjects.GameObject;
import sirius.SiriusTheFox;
import sirius.rendering.color.Color;
import sirius.rendering.debug.DebugDraw;
import org.joml.Vector2f;

public class Box2DCollider extends Collider2d {
    private Vector2f halfSize;
    private Vector2f origin;

    public Box2DCollider() {
        this.halfSize = new Vector2f(0.25f, 0.25f);
        this.origin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        GameObject activeGameObject = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObject();
        if (activeGameObject == null || activeGameObject != gameObject) return;

        Vector2f center = new Vector2f(gameObject.getPosition()).add(getOffset());
        DebugDraw.addBox2D(center, this.halfSize, this.gameObject.getRotation(), Color.DARK_GREEN);
    }

    /**
     * Gets the half width and the half height
     * @return halfSize vector
     */
    public Vector2f getHalfSize() {
        return halfSize;
    }

    /**
     * Sets half's size float vector
     * @param halfSize Sets halfSize vector
     */
    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    /**
     * Sets half's size float vector
     * @param halfWidth HalfWidth that we wish on halfSize.x vector
     * @param halfHeight HalfHeight that we wish on halfSize.y vector
     */
    public void setHalfSize(float halfWidth, float halfHeight) {
        this.halfSize.set(halfWidth, halfHeight);
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }
}
