package sirius.rendering.color;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sirius.SiriusTheFox;
import sirius.utils.AssetPool;

public class ColorBlindness {
    public static final ColorBlindnessCategories[] COLOR_BLINDNESSES;
    public static ColorBlindnessCategories selectedColorBlindness = ColorBlindnessCategories.NO_COLOR_BLINDNESS;
    public static ColorBlindnessCategories previousSelectedColorBlindness = selectedColorBlindness;
    public static int currentColorBlindness = 0;

    public static Matrix3f mXYZ;
    public static Matrix3f mLMSD65;
    public static Matrix3f sProtanopia;
    public static Matrix3f tProtanopia;
    public static Matrix3f tProtanomaly;
    public static Matrix3f sDeuterapia;
    public static Matrix3f tDeuterapia;
    public static Matrix3f tDeuteranomaly;
    public static Matrix3f sTritanopia;
    public static Matrix3f tTritanopia;
    public static Matrix3f tTritanomaly;
    public static Matrix3f tAchromatopsia;
    public static Matrix3f tAchromatomaly;

    static {
        /* Matrices from https://gist.github.com/Lokno/df7c3bfdc9ad32558bb7 and http://mkweb.bcgsc.ca/colorblind/math.mhtml
         * Matrices variables that begins with a 't' are from https://gist.github.com/Lokno/df7c3bfdc9ad32558bb7. This
         * matrices are already calculated and don't need more calculus.
         * Matrices variables that begins with a 's' are from http://mkweb.bcgsc.ca/colorblind/math.mhtml. This matrices
         * are more precisely but need some calculus before may be used.
        */
        mXYZ = new Matrix3f(
                0.4124564f, 0.3575761f, 0.1804375f,
                0.2126729f, 0.7151522f, 0.0721750f,
                0.0193339f, 0.1191920f, 0.9503041f
        );
        mLMSD65 = new Matrix3f(
                0.4002f, 0.7076f, -0.0808f,
               -0.2263f, 1.1653f,  0.0457f,
                0f     , 0f     ,  0.9182f
        );

        // Red-Blind
        sProtanopia = new Matrix3f(
                0f, 1.05118294f, -0.05116099f,
                0f, 1f, 0f,
                0f, 0f, 1f
        );
        tProtanopia = new Matrix3f(
                0.567f, 0.433f, 0.000f,
                0.558f, 0.442f, 0.000f,
                0.000f, 0.242f, 0.758f
        );

        // Red-Weak
        tProtanomaly = new Matrix3f(
                0.817f, 0.183f, 0.000f,
                0.333f, 0.667f, 0.000f,
                0.000f, 0.125f, 0.875f);

        // Green-Blind
        sDeuterapia = new Matrix3f(
                1f,         0f, 0f,
                0.9513092f, 0f, 0.04866992f,
                0f,         0f, 1f
        );
        tDeuterapia = new Matrix3f(
                0.625f, 0.375f, 0.000f,
                0.700f, 0.300f, 0.000f,
                0.000f, 0.300f, 0.700f
        );

        // Green-Weak
        tDeuteranomaly = new Matrix3f(
                0.800f, 0.200f, 0.000f,
                0.258f, 0.742f, 0.000f,
                0.000f, 0.142f, 0.858f
        );

        // Blue-Blind
        sTritanopia = new Matrix3f(
                1f,           0f,          0f,
                0f,           1f,          0f,
                -0.86744736f, 1.86727089f, 0f
        );
        tTritanopia = new Matrix3f(
                0.950f, 0.050f, 0.000f,
                0.000f, 0.433f, 0.567f,
                0.000f, 0.475f, 0.525f
        );

        // Blue-Weak
        tTritanomaly = new Matrix3f(
                0.967f, 0.033f, 0f,
                0f,     0.733f, 0.267f,
                0f,     0.183f, 0.817f
        );

        // Monochromacy
        tAchromatopsia = new Matrix3f(
                0.299f, 0.587f, 0.114f,
                0.299f, 0.587f, 0.114f,
                0.299f, 0.587f, 0.114f
        );

        // Blue Cone Monochromacy
        tAchromatomaly = new Matrix3f(
                0.618f, 0.320f,0.062f,
                0.163f, 0.775f,0.062f,
                0.163f, 0.320f,0.516f
        );

        COLOR_BLINDNESSES     = new ColorBlindnessCategories[12];
        COLOR_BLINDNESSES[0]  = ColorBlindnessCategories.NO_COLOR_BLINDNESS;
        COLOR_BLINDNESSES[1]  = ColorBlindnessCategories.PROTANOPIA_A;
        COLOR_BLINDNESSES[2]  = ColorBlindnessCategories.PROTANOPIA_B;
        COLOR_BLINDNESSES[3]  = ColorBlindnessCategories.PROTANOMALY;
        COLOR_BLINDNESSES[4]  = ColorBlindnessCategories.DEUTERANOPIA_A;
        COLOR_BLINDNESSES[5]  = ColorBlindnessCategories.DEUTERANOPIA_B;
        COLOR_BLINDNESSES[6]  = ColorBlindnessCategories.DEUTERANOMALY;
        COLOR_BLINDNESSES[7]  = ColorBlindnessCategories.TRITANOPIA_A;
        COLOR_BLINDNESSES[8]  = ColorBlindnessCategories.TRITANOPIA_B;
        COLOR_BLINDNESSES[9]  = ColorBlindnessCategories.TRITANOMALY;
        COLOR_BLINDNESSES[10] = ColorBlindnessCategories.ACHROMATOPSIA;
        COLOR_BLINDNESSES[11] = ColorBlindnessCategories.ACHROMATOMALY;
    }

    public static Vector3f mul(Matrix3f mat3f, Vector3f columnVector) {
        Vector3f newVec3f = new Vector3f();

        newVec3f.set(
                mat3f.m00 * columnVector.x + mat3f.m01 * columnVector.y + mat3f.m02 * columnVector.z,
                mat3f.m10 * columnVector.x + mat3f.m11 * columnVector.y + mat3f.m12 * columnVector.z,
                mat3f.m20 * columnVector.x + mat3f.m21 * columnVector.y + mat3f .m22 * columnVector.z
        );

        return newVec3f;
    }

    public static Color adaptColorBlindness(float r, float g, float b, float a) {
        Color correctedColor = new Color();

        // RGB to XYZ
        Vector3f rgb = new Vector3f(r, g, b);
        Vector3f xyz = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ), new Vector3f(rgb));
        // XYZ to LMSD65
        Vector3f lms = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65), new Vector3f(xyz));

        switch (selectedColorBlindness) {
            case NO_COLOR_BLINDNESS -> {
                return new Color(r, g, b, a);
            }

            case PROTANOPIA_A -> {
                Vector3f lmsCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.sProtanopia), new Vector3f(lms));
                Vector3f xyzCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65).invert(), new Vector3f(lmsCorrection));
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ).invert(), new Vector3f(xyzCorrection));

                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case PROTANOPIA_B -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tProtanopia), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case PROTANOMALY -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tProtanomaly), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case DEUTERANOPIA_A -> {
                Vector3f lmsCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.sDeuterapia), new Vector3f(lms));
                Vector3f xyzCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65).invert(), new Vector3f(lmsCorrection));
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ).invert(), new Vector3f(xyzCorrection));

                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case DEUTERANOPIA_B -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tDeuterapia), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case DEUTERANOMALY -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tDeuteranomaly), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case TRITANOPIA_A -> {
                Vector3f lmsCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.sTritanopia), new Vector3f(lms));
                Vector3f xyzCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65).invert(), new Vector3f(lmsCorrection));
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ).invert(), new Vector3f(xyzCorrection));

                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case TRITANOPIA_B -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tTritanopia), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case TRITANOMALY -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tTritanomaly), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case ACHROMATOPSIA -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tAchromatopsia), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }

            case ACHROMATOMALY -> {
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(tAchromatomaly), new Vector3f(r, g, b));
                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
            }
        }

        return new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static Vector4f adaptColorBlindness(Vector4f color) {
        Color correctedColor = adaptColorBlindness(new Color(color.x, color.y, color.z, color.w));
        return new Vector4f(correctedColor.getRed(), correctedColor.getGreen(), correctedColor.getBlue(), correctedColor.getOpacity());
    }

    public static Color adaptColorBlindness(Color color) {
        return adaptColorBlindness(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
    }

    public static Color adaptColorBlindness(byte red, byte green, byte blue, byte alpha) {
        float r = (red & 0xFF) / 255.0f;
        float g = (green & 0xFF) / 255.0f;
        float b = (blue & 0xFF) / 255.0f;
        float a = (alpha & 0xFF) / 255.0f;

        return adaptColorBlindness(r, g, b, a);
    }

    public void adaptImages() {
        if (selectedColorBlindness != previousSelectedColorBlindness) {
            AssetPool.clearTextures();
            SiriusTheFox.changeScene(SiriusTheFox.getCurrentScene().getSceneInitializer());
            previousSelectedColorBlindness = selectedColorBlindness;
        }
    }
}
