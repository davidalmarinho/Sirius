package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

public class AnimationBox {
    private String trigger;
    public float x, y;
    private float width, height;

    public AnimationBox(String trigger, float x, float y) {
        this.trigger = trigger;
        this.x = x;
        this.y = y;
        this.width = 128.0f;
        this.height = 128.0f;
    }
    public AnimationBox(String trigger, ImVec2 position) {
        this.trigger = trigger;
        this.x = position.x;
        this.y = position.y;
        this.width = 128.0f;
        this.height = 128.0f;
    }

    public void imgui(ImVec2 origin) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                ImColor.intToColor(255, 255, 0, 255), 20.0f);
        drawList.addText(origin.x + x - getWidth() / 6, origin.y + y,
                ImColor.intToColor(0, 0, 0, 255), "text");
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
