package gameobjects.components;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import sirius.editor.imgui.JImGui;
import sirius.rendering.BatchFont;
import sirius.rendering.Color;
import sirius.rendering.fonts.Font;
import sirius.rendering.fonts.Glyph;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

public class FontRenderer extends Component {
    private Font font;
    private float scale;
    private String fontpath;
    private int previousMaxTextLength;
    private int maxTextLength;
    private transient BatchFont batchFont;
    private Color color;
    private float paragraphSpacing = 0.5f;
    private float lineSpacing = 0.0f;
    private float charactersSpacing = 0.0f;

    private transient int currentItem = -1;

    public FontRenderer() {
        font = new Font(AssetPool.getFont(Settings.Files.CURRENT_FONT_PATH));
        this.maxTextLength = 32;
        this.previousMaxTextLength = maxTextLength;
        this.batchFont = new BatchFont(maxTextLength);
        this.scale = Settings.GameObjects.DEFAULT_FONT_SCALE;
        this.color = new Color(Color.BLACK);
    }

    @Override
    public void start() {
        this.batchFont.initBatch();
    }

    public void render() {
        TextBox textBox = gameObject.getComponent(TextBox.class);
        String text = textBox.getText();

        float greatestHeight = font.getGreatestHeight() * scale;

        final float TEXT_BOX_WIDTH = textBox.getWidth();

        // Split text in paragraphs using '\n' char
        String[] paragraphs = text.split("\n");

        int numLines = 0;

        for (String paragraph : paragraphs) {
            float currentWidth = 0.0f;

            // Keeps the index of the last character possible to render in the same line
            int lastCharIndex = 0;

            // Go throughout each character of the desired text to render
            for (int index = 0; index < paragraph.length(); index++) {
                Glyph glyph = batchFont.getGlyph(paragraph.charAt(index));
                if (glyph == null) continue;

                currentWidth += glyph.width * scale;

                // New line
                if (currentWidth > TEXT_BOX_WIDTH) {
                    batchFont.addText(paragraph.substring(lastCharIndex, index),
                            gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f,
                            gameObject.getPosition().y + textBox.getHeight() / 2.0f
                                    - greatestHeight
                                    - greatestHeight * numLines,
                            this.scale,
                            color);

                    currentWidth = glyph.width * scale;
                    lastCharIndex = index;
                    numLines++;
                }

                if (currentWidth < TEXT_BOX_WIDTH && index == paragraph.length() - 1) {
                    batchFont.addText(paragraph.substring(lastCharIndex), gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f,
                            gameObject.getPosition().y + textBox.getHeight() / 2.0f
                                    - greatestHeight
                                    - greatestHeight * numLines,
                            this.scale,
                            color);
                }
            }

            numLines++;
        }

        batchFont.flushBatch();
    }

    @Override
    public void imgui() {
        boolean reloadFont = false;

        // Select color
        JImGui.colorPicker4("Color: ", color);

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
            batchFont.reset(maxTextLength);
        }
    }
}
