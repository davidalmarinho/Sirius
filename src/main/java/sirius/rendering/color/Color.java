package sirius.rendering.color;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Color {
    private transient ColorBlindnessCategories colorBlindnessCategory = ColorBlindnessCategories.PROTANOPIA;

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
        float r = color.x;
        float g = color.y;
        float b = color.z;
        float a = color.w;

        // RGB to XYZ
        // Vector3f rgb = new Vector3f(r, g, b);
        // Vector3f xyz = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ), new Vector3f(rgb));
        // Vector3f lms = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65), new Vector3f(xyz));
        // Vector3f lmsCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.sProtanopia), new Vector3f(lms));
        // Vector3f xyzCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65).invert(), new Vector3f(lmsCorrection));
        // Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ).invert(), new Vector3f(xyzCorrection));
//
        // switch (colorBlindnessCategory) {
        //     case PROTANOPIA -> {
        //         return new Vector4f(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
        //     }
        // }

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

    public void convertToCMYK(Color color, ColorBlindnessCategories colorBlindness) {
        switch (colorBlindness) {
            case NO_COLOR_BLINDNESS -> {

            }
        }
    }

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
