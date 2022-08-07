package sirius.rendering.spritesheet;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
    private String filePath;
    private transient int textureID;
    private transient int width, height;

    public Texture(String filePath) {
        this.filePath = filePath;
        init();
    }

    public Texture(String filePath, BufferedImage image) {
        this.filePath = filePath;

        // Init texture based in an existing image
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * Integer.BYTES);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alphaComponent = (byte) ((pixel >> 24) & 0xFF);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
            }
        }
        buffer.flip();

        this.textureID = glGenTextures();

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        buffer.clear();
    }

    /**
     * Allocates just the space of a texture.
     * Created for especially {@link sirius.rendering.FrameBuffer}
     *
     * @param width width of the nonexistence image
     * @param height height of the nonexistence image
     */
    public Texture(int width, int height) {
        this.filePath = "Generated";

        // Generate texture
        this.textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    private void init() {
        // Create the texture
        this.textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL_TEXTURE_2D, textureID);

        // Configure all parameters
        // Repeat the image in the same directions
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // To in case if the image is pixelated
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // To in case that if the image is shrinked it keeps pixelated
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Load the image
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // One time that the image is rendered upside down --will turn the texture upside down
        STBImage.stbi_set_flip_vertically_on_load(true);

        ByteBuffer image = STBImage.stbi_load(this.filePath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);

            // We have data, so we can load the image
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

        // Clean the memory in GPU
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
