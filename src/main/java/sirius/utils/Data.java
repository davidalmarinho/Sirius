package sirius.utils;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Data {

    public static FloatBuffer toBuffer(float[] data) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);

        floatBuffer.put(data);
        floatBuffer.flip();

        return floatBuffer;
    }

    public static IntBuffer toBuffer(int[] data) {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(data.length);
        intBuffer.put(data).flip();

        return intBuffer;
    }
}
