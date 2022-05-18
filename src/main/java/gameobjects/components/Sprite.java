package gameobjects.components;

import sirius.rendering.spritesheet.Texture;
import org.joml.Vector2f;

import java.util.Arrays;

public class Sprite {
    private int width, height;
    private Texture texture;
    private Vector2f[] textureCoordinates;

    private Sprite() {

    }

    @Override
    public String toString() {
        return "Sprite{" +
                "width=" + width +
                ", height=" + height +
                ", texture=" + texture +
                ", textureCoordinates=" + Arrays.toString(textureCoordinates) +
                '}';
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

    private void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Vector2f[] getTextureCoordinates() {
        return textureCoordinates;
    }

    public void setTextureCoordinates(Vector2f[] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

    public int getTextureID() {
        return texture == null ? -1 : texture.getTextureID();
    }

    public static class Builder {
        private int width, height;
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

        public Builder setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Sprite build() {
            Sprite sprite = new Sprite();
            sprite.setTextureCoordinates(textureCoordinates);
            sprite.setTexture(texture);
            sprite.setSize(width, height);
            return sprite;
        }
    }
}
