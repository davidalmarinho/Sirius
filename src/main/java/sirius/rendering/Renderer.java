package sirius.rendering;

import gameobjects.GameObject;
import gameobjects.components.text_components.FontRenderer;
import gameobjects.components.SpriteRenderer;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import sirius.SiriusTheFox;
import sirius.rendering.color.Color;
import sirius.rendering.color.ColorBlindness;
import sirius.rendering.color.ColorBlindnessCategories;
import sirius.rendering.spritesheet.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Renderer {
    public static boolean changeColorBlindness;
    private ColorBlindnessCategories curColorBlindness = ColorBlindnessCategories.NO_COLOR_BLINDNESS;
    private ColorBlindnessCategories previousColorBlindness = ColorBlindnessCategories.NO_COLOR_BLINDNESS;

    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        batches = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

        if (spriteRenderer != null && !gameObject.hasComponent(FontRenderer.class)) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for (RenderBatch renderBatch : batches) {
            // If we have more free space in the actual renderBatch, we will put the sprite in that renderBatch
            if (renderBatch.hasRoom() && renderBatch.getzIndex() == spriteRenderer.gameObject.getTransform().zIndex) {
                Texture texture = spriteRenderer.getTexture();
                if (texture == null || renderBatch.hasRoomTexture() || renderBatch.hasTexture(texture)) {
                    renderBatch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            // Else, the actual render batch is full, and we need another one
            RenderBatch newRenderBatch = new RenderBatch(MAX_BATCH_SIZE,
                    spriteRenderer.gameObject.getTransform().zIndex, this);
            newRenderBatch.start();
            batches.add(newRenderBatch);
            newRenderBatch.addSprite(spriteRenderer);

            // Put in the right rendering order
            Collections.sort(batches);
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void renderUserInterface() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        currentShader.use();

        for (GameObject gameObject : SiriusTheFox.getCurrentScene().getGameObjectList()) {
            if (gameObject.hasComponent(FontRenderer.class)) {
                gameObject.getComponent(FontRenderer.class).render();
            }
        }

        currentShader.detach();
    }

    public void render() {
        currentShader.use();

        for (int i = 0; i < batches.size(); i++)
            batches.get(i).render();

        currentShader.detach();
    }

    // TODO: 08/08/2022 organize better where to put this method
    public void adaptColorBlindness() {
        // for (GameObject gameObject : SiriusTheFox.getCurrentScene().getGameObjectList()) {
        //     SpriteRenderer sr = gameObject.getComponent(SpriteRenderer.class);
        //     if (sr != null) {
        //         Color color = sr.getColor();
        //         // RGB to XYZ
        //         Vector3f rgb = new Vector3f(color.getRed(), color.getGreen(), color.getBlue());
        //         Vector3f xyz = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ), new Vector3f(rgb));
        //         Vector3f lms = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65), new Vector3f(xyz));
        //         Vector3f lmsCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.sProtanopia), new Vector3f(lms));
        //         Vector3f xyzCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mLMSD65).invert(), new Vector3f(lmsCorrection));
        //         Vector3f rgbCorrection = ColorBlindness.mul(new Matrix3f(ColorBlindness.mXYZ).invert(), new Vector3f(xyzCorrection));
        //
        //         sr.setColor(new Color(rgbCorrection.x, rgbCorrection.y, rgbCorrection.z, color.getOpacity()));
        //         sr.setDirty(true);
        //     }
        // }
    }

    public void destroyGameObject(GameObject go) {
        if (!go.hasComponent(SpriteRenderer.class)) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }
}
