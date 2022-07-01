package gameobjects.components.fonts;

import gameobjects.components.Component;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.joml.Vector2f;
import sirius.editor.imgui.JImGui;
import sirius.utils.AssetPool;

public class FontRenderer extends Component {
    private Vector2f position;
    private String showingText;
    private String fontpath;
    private int maxTextLength = 32;

    private transient int currentItem = -1;

    public FontRenderer() {
        this.position = new Vector2f();
        this.showingText = "I'm a text box!";
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
        // Set showing text
        JImGui.drawVec2Control("Position", this.position);
        this.showingText   = JImGui.inputText("Text Box:", this.showingText, maxTextLength);
        this.maxTextLength = JImGui.inputInt(1, "Max text length: ", this.maxTextLength);

        // Load desired font
        currentItem = JImGui.listOpenArrow("Font list:", currentItem, AssetPool.getFontsNames());
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
            this.fontpath = AssetPool.getFontsPaths()[currentItem];
            currentItem = -1;
        }
    }
}
