package jade.rendering;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import jade.rendering.spritesheet.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        batches = new ArrayList<>();
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
            if (renderBatch.hasRoom() && renderBatch.getzIndex() == spriteRenderer.gameObject.getzIndex()) {
                Texture texture = spriteRenderer.getTexture();
                if (texture == null ||renderBatch.hasRoomTexture() || renderBatch.hasTexture(texture)) {
                    renderBatch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            // Se não, o renderBatch atual está cheio e precisamos de outro
            RenderBatch newRenderBatch = new RenderBatch(MAX_BATCH_SIZE, spriteRenderer.gameObject.getzIndex());
            newRenderBatch.start();
            batches.add(newRenderBatch);
            newRenderBatch.addSprite(spriteRenderer);

            // Put in the right rendering order
            Collections.sort(batches);
        }
    }

    public void render() {
        for (RenderBatch renderBatch : batches) {
            renderBatch.render();
        }
    }
}