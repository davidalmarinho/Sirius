package jade.gameobjects.components;

import jade.renderer.Texture;
import org.joml.Vector2f;

public class Sprite {
    private Texture texture;
    private Vector2f[] textureCoordinates;

    public Sprite(Texture texture) {
        this.texture = texture;
        this.textureCoordinates = new Vector2f[]{
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public Sprite(Texture texture, Vector2f[] textureCoordinates) {
        this.texture = texture;
        this.textureCoordinates = textureCoordinates;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTextureCoordinates() {
        return textureCoordinates;
    }
}
