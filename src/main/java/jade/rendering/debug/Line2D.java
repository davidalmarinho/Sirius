package jade.rendering.debug;

import jade.rendering.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.sound.sampled.Line;

public class Line2D {
    private final Vector2f from, to;
    private Color color;
    private int lifeTime;

    /**
     * Constructor method for physics purposes
     *
     * @param from line's begin
     * @param to line's end
     */
    public Line2D(Vector2f from, Vector2f to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Constructor method for line rendering (debugging) purposes
     * @param from line's begin
     * @param to line's end
     * @param color line's color
     * @param lifeTime how much time we want to see the line in fps/seconds units
     */
    public Line2D(Vector2f from, Vector2f to, Color color, int lifeTime) {
        this(from, to);
        this.color = color;
        this.lifeTime = lifeTime;
    }

    public int beginFrame() {
        this.lifeTime--;
        return this.lifeTime;
    }


    public Vector2f getBegin() {
        return from;
    }

    public Vector2f getEnd() {
        return to;
    }

    public Vector3f getColor() {
        return color.getColor();
    }

    public int getLifeTime() {
        return lifeTime;
    }
}
