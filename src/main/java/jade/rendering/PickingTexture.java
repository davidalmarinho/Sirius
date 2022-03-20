package jade.rendering;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Each game object will have a color identifier. When the cursor is inside a game object,
 * the color will be identified and will be checked to what color it is identifying
 */
public class PickingTexture {
    private int pickingTextureId;
    private int fboId;
    private int depthTexture; // Normally used for 3d stuff

    // Same size as the fame buffer
    public PickingTexture(int width, int height) {
        if (!init(width, height)) {
            assert false : "Error initializing picking texture.";
        }
    }

    private boolean init(int width, int height) {
        // Generate framebuffer
        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Attach 2d texture to frame buffer
        pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Upload an empty image
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                pickingTextureId, 0);

        // Create the texture object for the depth buffer
        glEnable(GL_TEXTURE_2D); // In the case a sprite is behind another
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

        // Disable the reading
        glReadBuffer(GL_NONE); // Don't read from a framebuffer
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Check if this all has succeeded
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer isn't complete";
            return false;
        }

        // Unbind the texture and the framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return true;
    }

    public void enableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboId);
    }

    public void disableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public float[] readPixels(int xStart, int yStart, int xEnd, int yEnd) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        Vector2i size = new Vector2i(xEnd, yEnd).sub(new Vector2i(xStart, yStart)).absolute();

        // Pixels in the selected square
        int numPixels = size.x * size.y;

        float[] rgbPixels = new float[3 * numPixels];
        glReadPixels(xStart, yStart, size.x, size.y, GL_RGB, GL_FLOAT, rgbPixels);

        // We have to get the proper id from each game object
        for (int i = 0; i < rgbPixels.length; i++) {
            rgbPixels[i] -= 1;
        }

        return rgbPixels;
    }
}
