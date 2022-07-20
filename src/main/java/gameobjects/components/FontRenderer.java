package gameobjects.components;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.joml.Vector2f;
import sirius.editor.imgui.JImGui;
import sirius.rendering.fonts.Font;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

public class FontRenderer extends Component {
    private transient Font font;
    private float scale;
    private Vector2f position;
    private String showingText;
    private String fontpath;
    private int maxTextLength;

    private transient int currentItem = -1;

    public FontRenderer() {
        this.position = new Vector2f();
        this.showingText = "I'm a text box!";
        this.maxTextLength = 32;
        this.scale = Settings.GameObjects.DEFAULT_FONT_SCALE;
    }

    @Override
    public void start() {
        this.position.set(gameObject.getPosition());
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void imgui() {
        boolean reloadFont = false;

        // Set showing text
        JImGui.drawVec2Control("Position", this.position);
        this.showingText   = JImGui.inputText("Text Box:", this.showingText, maxTextLength);
        this.maxTextLength = JImGui.inputInt(1, "Max text length: ", this.maxTextLength);

        // Set the scale of the text
        this.scale = JImGui.dragFloat("Font Scale:", this.scale);

        // Load desired font
        currentItem = JImGui.listOpenArrow("Font list:", currentItem, AssetPool.getFontsNames());
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
            this.fontpath = AssetPool.getFontsPaths()[currentItem];
            reloadFont = true;
            currentItem = -1;
        }

        if (reloadFont) {
            font = AssetPool.getFont(this.fontpath);
        }
    }
}
