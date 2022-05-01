package sirius.imgui;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;

public class AnimationBox {
    private String trigger;
    public float x, y;
    private float width, height;

    public AnimationBox(String trigger) {
        this.trigger = trigger;
        this.x = ImGui.getMousePosX();
        System.out.println(x);
        this.y = ImGui.getMousePosY();
        this.width = 128.0f;
        this.height = 128.0f;
    }

    public void imgui() {
        // TODO: 01/05/2022 Convert to screen coordinates
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addText(x, y, ImColor.intToColor(255, 255, 255, 255), "text");
        drawList.addRectFilled(x - width / 2, y + height / 2,
                x + width / 2, y - height / 2, ImColor.intToColor(0, 255, 0, 255));
    }
}
