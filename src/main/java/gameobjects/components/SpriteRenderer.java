package gameobjects.components;

import sirius.editor.imgui.JImGui;
import sirius.rendering.Color;
import sirius.rendering.spritesheet.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = Sprite.Builder.newInstance().build();

    private transient Transform lastTransform;

    // Dirty Flag
    private transient boolean dirty = true;

    private SpriteRenderer() {

    }

    @Override
    public void start() {
        this.lastTransform = gameObject.getTransform().copy();
    }

    @Override
    public void editorUpdate(float dt) {
        if (!lastTransform.equals(gameObject.getTransform())) {
            this.gameObject.getTransform().copy(this.lastTransform);
            dirty = true;
        }
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.getTransform())) {
            this.gameObject.getTransform().copy(this.lastTransform);
            dirty = true;
        }
    }

    @Override
    public void imgui() {
        /*final float[] imColors = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Color Picker: ", imColors)) {
            this.color.set(imColors[0], imColors[1], imColors[2], imColors[3]);
            this.dirty = true;
        }*/

        if (JImGui.colorPicker4("Color Picker", this.color)) {
            this.dirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTextureCoordinates();
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.dirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.dirty = true;
        }
    }

    public void setColor(Color color) {
        Vector4f vec4Color = color.getColor();
        if (!this.color.equals(vec4Color)) {
            this.color.set(vec4Color);
            this.dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public static class Builder {
        private Vector4f color = new Vector4f(1, 1, 1, 1);
        private Sprite sprite = Sprite.Builder.newInstance().build();

        private Builder() {

        }

        public Builder setColor(Vector4f color) {
            this.color = color;
            return this;
        }

        public Builder setColor(int r, int g, int b, int a) {
            this.color.set(r, g, b, a);
            return this;
        }

        public Builder setSprite(Sprite sprite) {
            this.sprite = sprite;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public SpriteRenderer build() {
            SpriteRenderer spriteRenderer = new SpriteRenderer();
            spriteRenderer.setSprite(this.sprite);
            spriteRenderer.setColor(this.color);
            return spriteRenderer;
        }
    }
}
