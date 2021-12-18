package jade.gameobjects.components;

import jade.renderer.Texture;
import org.joml.Vector2f;

public class Sprite {
    private Texture texture;
    private Vector2f[] textureCoordinates;

    private Sprite() {

    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector2f[] getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates(Vector2f[] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

    public static class Builder {
        private Texture texture;
        private Vector2f[] textureCoordinates;

        private Builder() {
            this.texture = null;
            this.textureCoordinates = new Vector2f[]{
                    new Vector2f(1, 1),
                    new Vector2f(1, 0),
                    new Vector2f(0, 0),
                    new Vector2f(0, 1)
            };
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setTexture(Texture texture) {
            this.texture = texture;
            return this;
        }

        public Builder setTextureCoordinates(Vector2f[] textureCoordinates) {
            this.textureCoordinates = textureCoordinates;
            return this;
        }

        public Sprite build() {
            Sprite sprite = new Sprite();
            sprite.setTextureCoordinates(textureCoordinates);
            sprite.setTexture(texture);
            return sprite;
        }
    }
}
