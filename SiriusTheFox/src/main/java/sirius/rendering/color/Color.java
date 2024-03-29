package sirius.rendering.color;

import org.joml.Vector4f;

public class Color {
    public static Color WHITE;
    public static Color RED;
    public static Color DARK_GREEN;
    public static Color GREEN;
    public static Color BLUE;
    public static Color BLACK;
    public static Color NEON_PINK;

    private final Vector4f color;

    static {
        WHITE      = new Color(1.0f, 1.0f, 1.0f);
        RED        = new Color(1.0f, 0.0f, 0.0f);
        DARK_GREEN = new Color(0.08f, 0.27f, 0.2f);
        GREEN      = new Color(0.0f, 1.0f, 0.0f);
        BLUE       = new Color(0.0f, 0.0f, 1.0f);
        BLACK      = new Color(0.0f, 0.0f, 0.0f);
        NEON_PINK  = new Color(1.0f, 0.06f, 0.94f);
    }

    public Color(float r, float g, float b, float a) {
        this.color = new Vector4f(r, g, b, a);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Color(Color newColor) {
        this.color = new Vector4f(newColor.getColor());
    }

    public Vector4f getColor() {
        return this.color;
    }

    public int getDecimal32() {
        int opacity = (int) this.color.w << 24;
        int red     = (int) this.color.x << 16;
        int green   = (int) this.color.y << 8;
        int blue    = (int) this.color.z;

        return opacity + red + green + blue;
    }

    public int getDecimal16() {
        int red     = (int) (this.color.x * 255.0f) << 16;
        int green   = (int) (this.color.y * 255.0f) << 8;
        int blue    = (int) (this.color.z * 255.0f);

        return red + green + blue;
    }

    /*public int getDecimal() {
        int opacity = (int) (this.color.w / 255.0f) << 24;
        int red     = (int) (this.color.x / 255.0f) << 16;
        int green   = (int) (this.color.y / 255.0f) << 8;
        int blue    = (int) (this.color.z / 255.0f);

        return opacity + red + green + blue;
    }*/

    public float getRed() {
        return this.color.x;
    }

    public float getGreen() {
        return this.color.y;
    }

    public float getBlue() {
        return this.color.z;
    }

    public float getOpacity() {
        return this.color.w;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void setColor(Color newColor) {
        this.color.set(newColor.getColor().x, newColor.getColor().y, newColor.getColor().z, newColor.getColor().w);
    }
}
