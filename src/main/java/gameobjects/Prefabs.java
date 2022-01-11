package gameobjects;

import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getCurrentScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer spriteRenderer = SpriteRenderer.Builder.newInstance().setSprite(sprite).build();
        block.addComponent(spriteRenderer);

        return block;
    }
}
