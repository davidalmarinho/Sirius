package jade.rendering.spritesheet;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
    private String filePath;
    private transient int textureID;
    private int width, height;

    private Texture() {
        this.filePath = "";
    }

    public Texture(String filePath) {
        this.filePath = filePath;
        init();
    }

    /**
     * Allocates just the space of a texture.
     * Created for especially {@link jade.rendering.FrameBuffer}
     *
     * @param width width of the nonexistence image
     * @param height height of the nonexistence image
     */
    public Texture(int width, int height) {
        this.filePath = "Generated texture";

        // Generate texture
        this.textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL_TEXTURE_2D, textureID);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    private void init() {
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

        ByteBuffer image = STBImage.stbi_load(this.filePath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);

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
            assert false : "Error: (Texture) Couldn't load image '" + this.filePath + "'.";
        }

        // Limpar a memória no GPU
        STBImage.stbi_image_free(image);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Texture)) return false;

        Texture oTex = (Texture) o;
        return oTex.getWidth() == this.width && oTex.getHeight() == this.height
                && oTex.getTextureID() == this.textureID && oTex.getFilePath().equals(this.filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureID() {
        return textureID;
    }
}
