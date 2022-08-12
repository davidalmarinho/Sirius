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
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;
    private ColorBlindness colorBlindness;

    public Renderer() {
        batches = new ArrayList<>();
        colorBlindness = new ColorBlindness();
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
        colorBlindness.adaptImages();

        for (int i = 0; i < batches.size(); i++)
            batches.get(i).render();

        currentShader.detach();
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
