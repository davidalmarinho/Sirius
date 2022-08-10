package sirius.rendering.color;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public class ColorBlindness {
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
}
