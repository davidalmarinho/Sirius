package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

public class Point {
    private static int maxId = 0;
    private int id;
    public ImVec2 position;
    private float radius;

    public Point(ImVec2 position, float radius) {
        this.id = maxId;
        maxId++;
        this.position = position;
        this.radius = radius;
    }

    public Point() {
        this(new ImVec2(0, 0), 6.0f);
    }

    /**
     * Shows the points.
     */
    public void imgui(ImVec2 origin) {
        ImDrawList imDrawList = ImGui.getWindowDrawList();
        imDrawList.addCircleFilled(origin.x + position.x, origin.y + position.y,
                radius, ImColor.intToColor(247, 179, 43, 255));
    }

    public int getId() {
        return id;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
