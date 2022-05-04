package sirius.imgui.sprite_animation_window;

import imgui.ImVec2;
import org.jbox2d.common.Vec2;

public class Wire {
    private int id;
    private float startX, startY;
    private float endX, endY;

    public Wire(Vec2 start, Vec2 end) {
        this.startX = start.x;
        this.startY = start.y;
        this.endX   = end.x;
        this.endY   = end.y;
    }

    public ImVec2 getStart() {
        return new ImVec2(startX, startY);
    }

    public void setStart(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public ImVec2 getEnd() {
        return new ImVec2(endX, endY);
    }

    public void setEnd(float endX, float endY) {
        this.endX = endX;
        this.endY = endY;
    }
}
