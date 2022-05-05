package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import org.joml.Vector2f;

public class Point {
    private Vector2f position;
    private final float RADIUS;

    public Point(Vector2f position, float radius) {
        this.position = position;
        this.RADIUS = radius;
    }

    public Point() {
        this(new Vector2f(), 3.0f);
    }

    public void imgui() {
        ImDrawList imDrawList = ImGui.getWindowDrawList();
        imDrawList.addCircleFilled(position.x, position.y, RADIUS, ImColor.intToColor(0, 0, 255, 255));
    }

    public Vector2f getPosition() {
        return position;
    }
}
