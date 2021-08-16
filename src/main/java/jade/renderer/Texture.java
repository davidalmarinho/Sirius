package jade.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
    private final String FILEPATH;
    private int textureID;

    public Texture(String filePath) {
        this.FILEPATH = filePath;

        // Criar a textura
        this.textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL_TEXTURE_2D, textureID);

        // Configurar os parâmetros
        // Repetir a imagem nas mesmas direções
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Para o caso se a imagem aumentar ficar pixelizada
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Para o caso de a imagem diminuir ficar pixelizada
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Carregar a imagem
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // uma vez que ela vai ser renderizada ao contrário
        STBImage.stbi_set_flip_vertically_on_load(true); // Serve para virar a textura ao contrario

        ByteBuffer image = STBImage.stbi_load(FILEPATH, width, height, channels, 0);

        if (image != null) {
            // Temos dados, então podemos carregar a imagem
            if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'.";
            }
        } else {
            assert false : "Error: (Texture) Couldn't load image '" + FILEPATH + "'.";
        }

        // Limpar a memória no GPU
        STBImage.stbi_image_free(image);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
