package sirius.imgui.sprite_animation_window;

import imgui.ImVec2;

public class Wire {
    private float startX, startY;
    private float endX, endY;

    public Wire(ImVec2 start, ImVec2 end) {
        this.startX = start.x;
        this.startY = start.y;
        this.endX   = end.x;
        this.endY   = end.y;
    }

    public Wire() {
        this(new ImVec2(), new ImVec2());
    }

    public Wire(Wire wire) {
        this(wire.getStart(), wire.getEnd());
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
