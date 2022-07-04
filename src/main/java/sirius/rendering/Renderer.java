package sirius.rendering;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import sirius.SiriusTheFox;
import sirius.rendering.spritesheet.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private List<BatchFont> fontBatchList;
    private static Shader currentShader;

    public Renderer() {
        batches       = new ArrayList<>();
        fontBatchList = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

        if (spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for (RenderBatch renderBatch : batches) {
            // Se tivermos espaço no renderBatch atual, colocamos o sprite nesse
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
            // Else, the actual render batch is full and we need another one
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
        fontBatchList.get(0).addText(text, x, y, scale, 0xFFff00ff);

        fontBatchList.get(0).flushBatch();
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void renderUserInterface() {
        currentShader.use();

        if (fontBatchList.isEmpty()) {
            fontBatchList.add(new BatchFont());
            fontBatchList.get(0).initBatch();
        }
        SiriusTheFox.getCurrentScene().getRenderer()
                .addText("Test Text and I love it!", 0.1f, 0.1f, 0.009f, new Color(150, 150, 150));
    }

    public void render() {
        currentShader.use();

        for (int i = 0; i < batches.size(); i++)
            batches.get(i).render();
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
