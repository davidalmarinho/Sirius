package gameobjects.components.text_components;

import gameobjects.components.Component;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import sirius.editor.imgui.JImGui;
import sirius.rendering.BatchFont;
import sirius.rendering.color.Color;
import sirius.rendering.fonts.Font;
import sirius.rendering.fonts.Glyph;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

public class FontRenderer extends Component {
    private transient Font font;
    private float scale;
    private String fontpath;
    private int maxTextLength;
    private transient BatchFont batchFont;
    private Color color;
    private transient float greatestHeight;
    private float paragraphSpacing = 0.2f;
    private float lineSpacing = 0.0f;
    private float charactersSpacing = 0.0f;

    private int currentItem = -1;

    private transient String[] lines;
    private transient int numLines;

    public FontRenderer(String fontpath) {
        this.fontpath = fontpath;
        this.scale = Settings.GameObjects.DEFAULT_FONT_SCALE;
        this.color = new Color(Color.BLACK);
        this.maxTextLength = 32;
        this.lines = new String[0];
    }

    @Override
    public void start() {
        this.maxTextLength = gameObject.getComponent(TextBox.class).getText().length();

        // Since 'java.awt.Font' can't be serialized, we will have to create some objects manually with
        // the proprieties that were supposed to be saved.
        this.batchFont = new BatchFont(this.fontpath, maxTextLength);
        this.font = new Font(AssetPool.getFont(this.fontpath));
    }

    @Override
    public void editorUpdate(float dt) {
        this.maxTextLength = gameObject.getComponent(TextBox.class).getText().length();
    }

    public void render() {
        TextBox textBox = gameObject.getComponent(TextBox.class);
        String text = textBox.getText();

        this.greatestHeight = font.getGreatestHeight() * scale + lineSpacing;

        final float TEXT_BOX_WIDTH = textBox.getWidth();

        // Split text in paragraphs using '\n' char
        String[] paragraphs = text.split("\n");

        int numLines = 0;

        for (String paragraph : paragraphs) {
            float currentWidth = 0.0f;

            // This variable will become zero when used, because paragraphs spacing may just be done once
            float curParagraphSpacing = paragraphSpacing;

            // Keeps the index of the last character possible to render in the same line
            int lastCharIndex = 0;

            // Go throughout each character of the desired text to render
            for (int index = 0; index < paragraph.length(); index++) {
                Glyph glyph = batchFont.getGlyph(paragraph.charAt(index));
                if (glyph == null) continue;

                currentWidth += glyph.width * scale + charactersSpacing;

                // New line
                if (currentWidth + curParagraphSpacing > TEXT_BOX_WIDTH) {
                    String displayText = paragraph.substring(lastCharIndex, index);
                    batchFont.addText(displayText,
                            gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f + curParagraphSpacing,
                            gameObject.getPosition().y + textBox.getHeight() / 2.0f
                                    - greatestHeight
                                    - greatestHeight * numLines,
                            this.scale,
                            this.charactersSpacing,
                            color);

                    if (this.numLines > 0 && numLines < lines.length)
                        lines[numLines] = displayText;

                    currentWidth = glyph.width * scale + charactersSpacing;
                    curParagraphSpacing = 0.0f;
                    lastCharIndex = index;
                    numLines++;
                }

                if (currentWidth + curParagraphSpacing < TEXT_BOX_WIDTH && index == paragraph.length() - 1) {
                    String displayText = paragraph.substring(lastCharIndex);
                    batchFont.addText(displayText,
                            gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f + curParagraphSpacing,
                            gameObject.getPosition().y + textBox.getHeight() / 2.0f
                                    - greatestHeight
                                    - greatestHeight * numLines,
                            this.scale,
                            this.charactersSpacing,
                            color);
                    curParagraphSpacing = 0.0f;

                    if (this.numLines > 0 && numLines < lines.length)
                        lines[numLines] = displayText;
                }
            }

            numLines++;
        }

        // Resize lines array
        if (numLines != this.numLines) {
            this.numLines = numLines;
            lines = new String[this.numLines];
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

        // Set the desired paragraph spacing
        this.paragraphSpacing = JImGui.dragFloat("Paragraph Spacing:", this.paragraphSpacing, 0.01f);
        if (this.paragraphSpacing < 0.0f) {
            this.paragraphSpacing = 0.0f;
        }

        // Set the desired characters spacing
        this.charactersSpacing = JImGui.dragFloat("Characters Spacing:", this.charactersSpacing, 0.01f);

        // Set the desired line spacing
        this.lineSpacing = JImGui.dragFloat("Line Spacing:", this.lineSpacing, 0.01f);

        // Load desired font
        currentItem = JImGui.listOpenArrow("Font list:", currentItem, AssetPool.getFontsNames());
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
            this.fontpath = AssetPool.getFontsPaths()[currentItem];
            reloadFont = true;
            currentItem = -1;
        }

        if (reloadFont) {
            font = new Font(AssetPool.getFont(this.fontpath));
            batchFont.filepath = this.fontpath;
            batchFont.reset(this.fontpath, 32);
        }
    }

    public int getLineIndex(int characterTextIndex) {
        int lineIndex = 0;
        int currentCharIndex = 0;
        for (int i = 0; i < lines.length; i++) {
            char[] currentLine = lines[i].toCharArray();
            for (int j = 0; j < currentLine.length; j++) {
                if (currentCharIndex == characterTextIndex) {
                    lineIndex = i;
                    break;
                }
                currentCharIndex++;
            }

            if (currentCharIndex == characterTextIndex) {
                lineIndex = i;
                break;
            }
        }

        return lineIndex;
    }

    public int getNumCharsCurrentLine(int index) {
        return lines[getLineIndex(index)].length();
    }

    public int getNumCharsPreviousLine(int index) {
        int lineIndex = getLineIndex(index);
        int previousLineIndex = lineIndex;
        if (lineIndex > 0) {
            previousLineIndex--;
        }

        return lines[previousLineIndex].length();
    }

    public float getCharactersSpacing() {
        return charactersSpacing;
    }

    public String[] getLines() {
        return lines;
    }

    public float getParagraphSpacing() {
        return paragraphSpacing;
    }

    public float getScale() {
        return scale;
    }

    public BatchFont getBatchFont() {
        return batchFont;
    }

    public float getGreatestHeight() {
        return greatestHeight;
    }
}
