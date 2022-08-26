package gameobjects.components;

import sirius.rendering.spritesheet.Texture;
import org.joml.Vector2f;

public class Sprite {
    private int width, height;
    private Texture texture;
    private Vector2f[] textureCoordinates;

    public Sprite() {
        this(0, 0, null, new Vector2f[] {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        });
    }

    public Sprite(Sprite newSprite) {
        this(newSprite.getWidth(), newSprite.getHeight(),
                new Texture(newSprite.texture.getFilePath()),
                newSprite.textureCoordinates);
    }

    public Sprite(int width, int height, Texture texture, Vector2f[] textureCoordinates) {
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.textureCoordinates = textureCoordinates;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2f[] getTextureCoordinates() {
        return textureCoordinates;
    }

    public int getTextureID() {
        return texture == null ? -1 : texture.getTextureID();
    }
}
