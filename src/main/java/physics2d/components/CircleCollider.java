package physics2d.components;

import gameobjects.GameObject;
import jade.SiriusTheFox;
import jade.Window;
import jade.rendering.Color;
import jade.rendering.debug.DebugDraw;
import org.joml.Vector2f;

public class CircleCollider extends Collider2d {
    private float radius = 1.0f;

    @Override
    public void editorUpdate(float dt) {
        GameObject activeGameObject = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObject();
        if (activeGameObject == null || activeGameObject != gameObject) return;

        Vector2f center = new Vector2f(gameObject.transform.position).add(getOffset());
        DebugDraw.addCircle(center, radius, Color.GREEN);
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
        this.radius = radius;
    }
}
