package jade.rendering.debug;

import jade.rendering.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
    private final Vector2f from, to;
    private final Color color;
    private int lifeTime;

    public Line2D(Vector2f from, Vector2f to, Color color, int lifeTime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifeTime = lifeTime;
    }

    public int beginFrame() {
        this.lifeTime--;
        return this.lifeTime;
    }


    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color.getColor();
    }

    public int getLifeTime() {
        return lifeTime;
    }
}
