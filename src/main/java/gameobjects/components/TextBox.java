package gameobjects.components;

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

    public TextBox(String text, float width, float height) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.maxTextLength = text.length();
        this.maxIndex = text.length() - 1;
        // TODO: 29/07/2022 Better system than this debounce system
        this.DEBOUNCE  = 0.20f;
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

        // Navigate in the text to select the current character
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
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

        // Delete current character
        } else if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                delCurrentChar();
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                write('\n');
            }
        } else if (KeyListener.isAnyKeyPressed()) {
            curDebounce -= dt;
            if (curDebounce < 0) {
                curDebounce = DEBOUNCE;
                write(KeyListener.getPressedKey());
            }

        } else {
            curDebounce = 0;
        }

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

    private void moveLeft() {
        if (index >= 0)
            index--;
        else
            index = maxIndex;
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
        this.height = JImGui.dragFloat("Height: ", this.height);
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
