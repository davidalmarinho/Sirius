package sirius.editor.imgui;

import imgui.type.ImBoolean;

public class GuiWindow {
    protected ImBoolean show;

    public GuiWindow(boolean show) {
        this.show = new ImBoolean(show);
    }

    public GuiWindow() {
        this.show = new ImBoolean(true);
    }

    public boolean isVisible() {
        return show.get();
    }

    public void setVisibility(boolean visibility) {
        this.show.set(visibility);
    }
}
