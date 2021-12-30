package jade.rendering;

import org.joml.Vector3f;

public class Color {
    public static Color WHITE = new Color(1.0f, 1.0f, 1.0f);
    public static Color RED   = new Color(1.0f, 0.0f, 0.0f);
    public static Color GREEN = new Color(0.0f, 1.0f, 0.0f);
    public static Color BLUE  = new Color(0.0f, 0.0f, 1.0f);
    public static Color BLACK = new Color(0.0f, 0.0f, 0.0f);

    private final Vector3f color;

    public Color(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
    }

    public Vector3f getColor() {
        return this.color;
    }
}
