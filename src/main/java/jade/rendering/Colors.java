package jade.rendering;

import org.joml.Vector3f;

public enum Colors {
    WHITE(new Vector3f (1.0f, 1.0f, 1.0f)),
    BLACK(new Vector3f(0.0f, 0.0f, 0.0f)),
    RED(new Vector3f(1.0f, 0.0f, 0.0f)),
    GREEN(new Vector3f(0.0f, 1.0f, 0.0f)),
    BLUE(new Vector3f(0.0f, 0.0f, 1.0f));

    private final Vector3f color;
    Colors(Vector3f color) {
        this.color = color;
    }

    public Vector3f getColor() {
        return color;
    }
}
