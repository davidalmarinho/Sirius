package gameobjects;

import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = new GameObject("Sprite_Object_Gen",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);

        SpriteRenderer spriteRenderer = SpriteRenderer.Builder.newInstance().setSprite(sprite).build();
        block.addComponent(spriteRenderer);

        return block;
    }
}
