package sirius.rendering;

import org.joml.Vector4f;

public class Color {
    public static Color WHITE      = new Color(1.0f, 1.0f, 1.0f);
    public static Color RED        = new Color(1.0f, 0.0f, 0.0f);
    public static Color DARK_GREEN = new Color(0.08f, 0.27f, 0.2f);
    public static Color GREEN      = new Color(0.0f, 1.0f, 0.0f);
    public static Color BLUE       = new Color(0.0f, 0.0f, 1.0f);
    public static Color BLACK      = new Color(0.0f, 0.0f, 0.0f);
    public static Color NEON_PINK  = new Color(1.0f, 0.06f, 0.94f);

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

    // TODO: 27/04/2022 Broken
    public int getDecimal32() {
        int opacity = (int) (this.color.w * 255.0f) << 24;
        int red     = (int) (this.color.x * 255.0f) << 16;
        int green   = (int) (this.color.y * 255.0f) << 8;
        int blue    = (int) (this.color.z * 255.0f);

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

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void setColor(Color newColor) {
        this.color.set(newColor.getColor().x, newColor.getColor().y, newColor.getColor().z, newColor.getColor().w);
    }
}
