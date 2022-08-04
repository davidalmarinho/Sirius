package gameobjects.components.text_components;

import gameobjects.components.Component;
import org.joml.Vector2f;
import sirius.editor.imgui.JImGui;
import sirius.input.KeyListener;
import sirius.rendering.Color;
import sirius.rendering.debug.DebugDraw;

import static org.lwjgl.glfw.GLFW.*;

public class TextBox extends Component {
    private String text;
    private float width, height;

    private int maxTextLength;
    private int maxIndex;
    private int index;
    private float curDebounce;
    private final float DEBOUNCE;

    private final float BLINKING_TIME = 0.8f;
    private float blinking = 0.0f;

    public TextBox(String text, float width, float height) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.maxTextLength = text.length();
        this.maxIndex = text.length() - 1;
        this.DEBOUNCE  = 0.02f;
    }

    public TextBox(float width, float height) {
        this("", width, height);
    }

    @Override
    public void start() {

    }

    @Override
    public void editorUpdate(float dt) {
        // Update always the maxIndex because we can add and remove characters from the text
        maxIndex = text.length() - 1;

        DebugDraw.addBox2D(new Vector2f(gameObject.getPosition().x, gameObject.getPosition().y),
                new Vector2f(width, height), 0.0f, Color.BLACK);

        FontRenderer fontRenderer = gameObject.getComponent(FontRenderer.class);

        float xCurrentAdvance = 0.0f;
        float yCurrentAdvance = 0.0f;
        char[] textInChars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            // When beginning text box, we always have a paragraph
            if (i == 0) {
                xCurrentAdvance += fontRenderer.getParagraphSpacing();
            }

            char curChar = textInChars[i];

            float curCharWidth = fontRenderer.getBatchFont().getGlyph(curChar).width * fontRenderer.getScale();
            if (i <= index) {
                xCurrentAdvance += curCharWidth;

                // Align insertion point with the paragraph
                if (curChar == '\n') {
                    xCurrentAdvance = fontRenderer.getParagraphSpacing();
                    yCurrentAdvance += fontRenderer.getGreatestHeight();
                }
            }

            // New line
            if (xCurrentAdvance > this.width) {
                xCurrentAdvance = curCharWidth;
                yCurrentAdvance += fontRenderer.getGreatestHeight();
            }
        }

        // Blinking insertion point
        blinking -= dt;
        if (blinking < -0.3f) {
            blinking = BLINKING_TIME;
        }

        // When writing, the insertion point won't blink
        if (KeyListener.isAnyKeyPressed()) {
            blinking = BLINKING_TIME;
        }

        if (blinking >= 0) {
            DebugDraw.addLine2D(
                    new Vector2f(
                            gameObject.getPosition().x - this.width / 2.0f + xCurrentAdvance,
                            gameObject.getPosition().y + this.height / 2.0f - yCurrentAdvance),
                    new Vector2f(
                            gameObject.getPosition().x - this.width / 2.0f + xCurrentAdvance,
                            gameObject.getPosition().y + this.height / 2.0f
                                    - fontRenderer.getGreatestHeight() - yCurrentAdvance),
                    new Color(Color.BLUE));
        }

        // Delete current character
        if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                delCurrentChar();
            }

        // Make paragraph
        } else if (KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                write('\n');
            }

        // Navigation controls
        } else if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_RIGHT)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                moveRightBind();
            }
        } else if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_LEFT)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                moveLeftBind();
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                moveRight();
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                moveLeft();
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
                curDebounce -= dt;
                if (curDebounce < 0) {
                    curDebounce = DEBOUNCE;
                    moveUp();
                }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                moveDown();
            }

        // Type
        } else if (KeyListener.isAnyKeyPressed()) {
            write(KeyListener.getPressedKey());
        }

        if (KeyListener.isAnyKeyReleased()) {
            curDebounce = 0;
        }

        // TODO: 03/08/2022 Implement:
        // Better system than debounce system
        // UP key                     [X]
        // DOWN key                   [X]
        // CTRL + RIGHT bind          [X]
        // CTRL + LEFT bind           [X]
        // SHIFT + RIGHT BIND
        // SHIFT + LEFT bind
        // CTRL + SHIFT + RIGHT bind
        // CTRL + SHIFT + LEFT bind
        // END key
        // HOME key
        // SHIFT + END
        // SHIFT + HOME
        // TAB --4 spaces

        if (!text.isEmpty()) {
            if (index == -1) {
                // System.out.println("Current char: ' ' ");
            } else {
                // System.out.println("Current char: '" + text.charAt(index) + "' ");
            }
        }
    }

    private void moveRight() {
        if (index < maxIndex)
            index++;
        else
            index = -1;
    }

    private void moveRightBind() {
        char[] textInChars = text.toCharArray();
        final int START = index + 1;
        for (int i = START; i < textInChars.length; i++) {
            if (textInChars[i] == ' ' && i > START) {
                break;
            }
            moveRight();
        }
    }

    private void moveLeftBind() {
        char[] textInChars = text.toCharArray();
        final int START = index;
        for (int i = START; i >= 0; i--) {
            if (textInChars[i] == ' ' && i < START) {
                break;
            }
            moveLeft();
        }
    }

    private void moveLeft() {
        if (index >= 0)
            index--;
        else
            index = maxIndex;
    }

    private void moveUp() {
        int numCharsPreviousLine = gameObject.getComponent(FontRenderer.class).getNumCharsPreviousLine(index);
        // System.out.println("Previous Line: " + numCharsPreviousLine);
        if (index - numCharsPreviousLine >= 0) {
            index -= numCharsPreviousLine;
        }
    }

    private void moveDown() {
        int numCharsCurrentLine = gameObject.getComponent(FontRenderer.class).getNumCharsCurrentLine(index);
        if (index + numCharsCurrentLine < text.length()) {
            index += numCharsCurrentLine;
        }
    }

    private void delCurrentChar() {
        char[] characters = text.toCharArray();
        StringBuilder newText = new StringBuilder();

        // Go throughout each character and check what character is marked to be deleted
        for (int i = 0; i < characters.length; i++) {
            if (i == index) {
                index = i - 1;
                continue;
            }

            newText.append(characters[i]);
        }
        this.text = newText.toString();
    }

    private void write(char key) {
        StringBuilder newText = new StringBuilder();

        if (key == '\0') {
            return;
        }

        for (int i = 0; i < text.length() + 1; i++) {
            if (i - 1 < index) {
                newText.append(text.charAt(i));
            } else if (i - 1 == index) {
                newText.append(key);
            } else {
                newText.append(text.charAt(i - 1));
            }
        }

        // New character was placed, so we want the index in that character
        index++;

        this.text = newText.toString();
    }

    @Override
    public void imgui() {
        // Set showing text
        this.text          = JImGui.inputText("Text Box:", this.text, maxTextLength);
        this.maxTextLength = JImGui.inputInt(1, "Max text length: ", this.maxTextLength);

        // Set text box's size
        this.width = JImGui.dragFloat("Width: ", this.width);
        if (width < 0.0f) {
            width = 0.0f;
        }

        this.height = JImGui.dragFloat("Height: ", this.height);
        if (height < 0.0f) {
            height = 0.0f;
        }
    }

    public String getText() {
        return text;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
