package sirius.editor.imgui.sprite_animation_window;

public class Wire {
    private Point from;
    private Point to;

    public Wire(Point from, Point to) {
        this.from = from;
        this.to   = to;
    }

    public Wire() {
        this(new Point(), new Point());
    }

    public Wire(Wire newWire) {
        this.from = new Point(newWire.getStartPoint());
        this.to   = new Point(newWire.getEndPoint());
    }

    public float getStartX() {
        return from.position.x;
    }

    public float getStartY() {
        return from.position.y;
    }

    public void setStart(float startX, float startY) {
        this.from.position.x = startX;
        this.from.position.y = startY;
    }

    public float getEndX() {
        return to.position.x;
    }

    public float getEndY() {
        return to.position.y;
    }

    public void setEnd(float endX, float endY) {
        this.to.position.x = endX;
        this.to.position.y = endY;
    }

    public Point getStartPoint() {
        return this.from;
    }

    public void setStartPoint(Point newPoint) {
        this.from = newPoint;
    }

    public Point getEndPoint() {
        return this.to;
    }

    public void setEndPoint(Point newPoint) {
        this.to = newPoint;
    }
}
