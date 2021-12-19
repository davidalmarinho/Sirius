package jade.gameobjects.components;

import jade.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {

    private Texture parentTex;
    private List<Sprite> spriteList;

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        this.spriteList = new ArrayList<>();

        this.parentTex = texture;

        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;

        for (int i = 0; i < numSprites; i++) {
            // Sprite's right top corner
            Vector2f rightTop = new Vector2f(
                    (currentX + spriteWidth) / (float) texture.getWidth(),
                    (currentY + spriteHeight) / (float) texture.getHeight()
            );

            // Sprite's left bottom corner
            Vector2f leftBottom = new Vector2f(
                    currentX / (float) texture.getWidth(),
                    currentY / (float) texture.getHeight()
            );

            Vector2f[] texCoords = {
                    new Vector2f(rightTop.x, rightTop.y),
                    new Vector2f(rightTop.x, leftBottom.y),
                    new Vector2f(leftBottom.x, leftBottom.y),
                    new Vector2f(leftBottom.x, rightTop.y)
            };
            Sprite sprite = Sprite.Builder.newInstance().setTexture(parentTex).setTextureCoordinates(texCoords).build();
            this.spriteList.add(sprite);

            // Catch next sprite
            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return this.spriteList.get(index);
    }
}
