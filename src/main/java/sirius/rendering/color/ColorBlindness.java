package sirius.rendering.color;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import gameobjects.components.text_components.FontRenderer;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sirius.SiriusTheFox;

import java.util.List;

public class ColorBlindness {
    public static ColorBlindnessCategories selectedColorBlindness = ColorBlindnessCategories.NO_COLOR_BLINDNESS;
    public static ColorBlindnessCategories previousSelectedColorBlindness = selectedColorBlindness;
    public static int currentColorBlindness = 0;

    public static Matrix3f mXYZ;
    public static Matrix3f mLMSD65;
    public static Matrix3f sProtanopia;

    static {
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

        sProtanopia = new Matrix3f(
                0f, 1.05118294f, -0.05116099f,
                0f, 1f, 0f,
                0f, 0f, 1f
        );
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

    public static Vector4f adaptColorBlindness(Vector4f color) {
        Color correctedColor = adaptColorBlindness(new Color(color.x, color.y, color.z, color.w));
        return new Vector4f(correctedColor.getRed(), correctedColor.getGreen(), correctedColor.getBlue(), correctedColor.getOpacity());
    }

    public static Color adaptColorBlindness(Color color) {
        // float r = color.x;
        // float g = color.y;
        // float b = color.z;
        // float a = color.w;

       // if (selectedColorBlindness != previousSelectedColorBlindness) {
       //     List<GameObject> gameObjectList = SiriusTheFox.getCurrentScene().getGameObjectList();
       //     for (GameObject g : gameObjectList) {
       //         SpriteRenderer sr = g.getComponent(SpriteRenderer.class);
       //         if (sr != null) {
       //             sr.setDirty(true);
       //         }
       //     }
//
       //     previousSelectedColorBlindness = selectedColorBlindness;
       // }

        float r = color.getRed();
        float g = color.getGreen();
        float b = color.getBlue();
        float a = color.getOpacity();

        Color correctedColor = new Color();

        // RGB to XYZ
        Vector3f rgb = new Vector3f(r, g, b);
        Vector3f xyz = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ), new Vector3f(rgb));
        Vector3f lms = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65), new Vector3f(xyz));

        switch (selectedColorBlindness) {
            case PROTANOPIA -> {
                Vector3f lmsCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.sProtanopia), new Vector3f(lms));
                Vector3f xyzCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65).invert(), new Vector3f(lmsCorrection));
                Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ).invert(), new Vector3f(xyzCorrection));

                correctedColor.setColor(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, a);
                return correctedColor;
            }
        }

        return color;
    }
}
