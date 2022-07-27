package sirius.rendering;

import gameobjects.GameObject;
import gameobjects.components.FontRenderer;
import gameobjects.components.SpriteRenderer;
import sirius.SiriusTheFox;
import sirius.rendering.spritesheet.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private BatchFont fontBatch;
    private static Shader currentShader;

    public Renderer() {
        batches = new ArrayList<>();
        fontBatch = new BatchFont();
        // fontBatch.initBatch();
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
                if (texture == null ||renderBatch.hasRoomTexture() || renderBatch.hasTexture(texture)) {
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

    public void addText(String text, float x, float y, float scale, Color color) {
        fontBatch.addText(text, x, y, scale, color);
        // fontBatch.flushBatch();
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void renderUserInterface() {
        //glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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

    public void destroyGameObject(GameObject go) {
        if (!go.hasComponent(SpriteRenderer.class)) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }
}
