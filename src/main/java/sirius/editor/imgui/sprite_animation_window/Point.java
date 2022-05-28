package sirius.editor.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

public class Point {
    private transient static int maxId = 0;
    private transient int id;
    public ImVec2 position;
    private transient float radius;

    public Point(ImVec2 position, float radius) {
        this.id       = maxId;
        this.position = position;
        this.radius   = radius;
        maxId++;
    }

    public Point(Point newPoint) {
        this.id       = newPoint.id;
        this.position = new ImVec2(newPoint.position);
        this.radius   = newPoint.radius;
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
        /*imDrawList.addTriangleFilled(
                origin.x + position.x + 5, origin.y + position.y + 5,
                origin.x + position.x + 5 + 10, origin.y + position.y - 5 - 10,
                origin.x + position.x + 5 + 20, origin.y + position.y + 5,
                ImColor.intToColor(247, 179, 43, 255));*/
    }

    public int getId() {
        return id;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
