package sirius.editor;

import gameobjects.components.Sprite;
import sirius.editor.imgui.PropertiesWindow;
import sirius.input.MouseListener;

class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            // Dragging x axis gizmo
            if (xAxisActive && !yAxisActive)
                activeGameObject.transform(-MouseListener.getGameViewportDeltaX(), 0);
            // Dragging y axis gizmo
            else if (yAxisActive)
                activeGameObject.transform(0, -MouseListener.getGameViewportDeltaY());
        }
        super.editorUpdate(dt);
    }
}
