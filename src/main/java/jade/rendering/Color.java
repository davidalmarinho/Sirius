package jade.rendering;

import org.joml.Vector4f;

public class Color {
    public static Color WHITE = new Color(1.0f, 1.0f, 1.0f);
    public static Color RED   = new Color(1.0f, 0.0f, 0.0f);
    public static Color GREEN = new Color(0.0f, 1.0f, 0.0f);
    public static Color BLUE  = new Color(0.0f, 0.0f, 1.0f);
    public static Color BLACK = new Color(0.0f, 0.0f, 0.0f);

    private final Vector4f color;

    public Color(float r, float g, float b, float a) {
        this.color = new Vector4f(r, g, b, a);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Vector4f getColor() {
        return this.color;
    }
}
