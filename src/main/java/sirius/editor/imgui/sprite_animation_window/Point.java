package sirius.editor.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

public class Point {
    private String origin;
    public static int maxId = 0;
    private final int ID;
    public ImVec2 position;
    private float radius;

    public Point(String origin, ImVec2 position, float radius) {
        this.origin   = origin;
        this.ID = maxId;
        this.position = position;
        this.radius   = radius;
        maxId++;
    }

    public Point(Point newPoint) {
        this.origin   = newPoint.origin;
        this.ID       = newPoint.ID;
        this.position = new ImVec2(newPoint.position);
        this.radius   = newPoint.radius;
    }

    public Point(String origin) {
        this(origin, new ImVec2(0, 0), 6.0f);
    }

    /**
     * Shows the points.
     */
    public void imgui(ImVec2 origin) {
        ImDrawList imDrawList = ImGui.getWindowDrawList();
        imDrawList.addCircleFilled(origin.x + position.x, origin.y + position.y,
                6.0f, ImColor.intToColor(247, 179, 43, 255));
        /*imDrawList.addTriangleFilled(
                origin.x + position.x + 5, origin.y + position.y + 5,
                origin.x + position.x + 5 + 10, origin.y + position.y - 5 - 10,
                origin.x + position.x + 5 + 20, origin.y + position.y + 5,
                ImColor.intToColor(247, 179, 43, 255));*/
    }

    public int getId() {
        return ID;
    }

    public String getOrigin() {
        return origin;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
