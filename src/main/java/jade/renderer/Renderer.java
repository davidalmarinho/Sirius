package jade.renderer;

import jade.gameobjects.GameObject;
import jade.gameobjects.components.SpriteRenderer;

import java.util.ArrayList;
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
            if (renderBatch.hasRoom()) {
                renderBatch.addSprite(spriteRenderer);
                added = true;
                break;
            }
        }

        if (!added) {
            // Se não, o renderBatch atual está cheio e precisamos de outro
            RenderBatch newRenderBatch = new RenderBatch(MAX_BATCH_SIZE);
            newRenderBatch.start();
            batches.add(newRenderBatch);
            newRenderBatch.addSprite(spriteRenderer);
        }
    }

    public void render() {
        for (RenderBatch renderBatch : batches) {
            renderBatch.render();
        }
    }
}