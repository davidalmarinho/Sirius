package sirius.utils;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Data {

    public static FloatBuffer toBuffer(float[] data) {
        // Criar o espaço na memória
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);

        // Para armazenar na memória criada anteriormente
        floatBuffer.put(data);

        // Para organizar e dizer que já não o vamos editar mais
        floatBuffer.flip();

        return floatBuffer;
    }

    public static IntBuffer toBuffer(int[] data) {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(data.length);
        intBuffer.put(data).flip();

        return intBuffer;
    }
}
