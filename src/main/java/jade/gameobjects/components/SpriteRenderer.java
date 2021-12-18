package jade.gameobjects.components;

import imgui.ImGui;
import jade.gameobjects.Transform;
import jade.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;

    private Transform lastTransform;

    // Dirty Flag
    private boolean dirty;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
        this.dirty = true;
    }

    public SpriteRenderer(Sprite sprite) {
        this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.sprite = sprite;
        this.dirty = true;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            dirty = true;
        }
    }

    @Override
    public void imgui() {
        final float[] imColors = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Color Picker: ", imColors)) {
            this.color.set(imColors[0], imColors[1], imColors[2], imColors[3]);
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

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
