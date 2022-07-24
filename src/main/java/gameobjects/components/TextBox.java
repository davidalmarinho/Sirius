package gameobjects.components;

import org.joml.Vector2f;
import sirius.editor.imgui.JImGui;
import sirius.rendering.Color;
import sirius.rendering.debug.DebugDraw;

public class TextBox extends Component {
    public String text;
    public float width, height;

    private int maxTextLength;

    public TextBox(String text, float width, float height) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.maxTextLength = 32;
    }

    public TextBox(float width, float height) {
        this("", width, height);
    }

    @Override
    public void start() {

    }

    @Override
    public void editorUpdate(float dt) {
        DebugDraw.addBox2D(new Vector2f(gameObject.getPosition().x, gameObject.getPosition().y),
                new Vector2f(width, height), 0.0f, Color.BLACK);
    }

    @Override
    public void imgui() {
        boolean reloadFont = false;
        // Set showing text
        this.text   = JImGui.inputText("Text Box:", this.text, maxTextLength);
        this.maxTextLength = JImGui.inputInt(1, "Max text length: ", this.maxTextLength);
    }
}
