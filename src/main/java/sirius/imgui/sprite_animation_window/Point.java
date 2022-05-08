package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

public class Point {
    private ImVec2 position;
    private float radius;

    public Point(ImVec2 position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    /**
     * Shows the points.
     * @param scrolling Variable that keeps how far we are from the initial position of the SpriteAnimationWindow's canvas
     */
    public void imgui(ImVec2 scrolling) {
        ImDrawList imDrawList = ImGui.getWindowDrawList();
        imDrawList.addCircleFilled(scrolling.x + position.x, scrolling.y + position.y,
                radius, ImColor.intToColor(247, 179, 43, 255));
    }

    public ImVec2 getPosition() {
        return position;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
