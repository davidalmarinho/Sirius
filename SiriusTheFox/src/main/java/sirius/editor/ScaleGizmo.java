package sirius.editor;

import gameobjects.components.Sprite;
import sirius.editor.imgui.PropertiesWindow;
import sirius.input.MouseListener;

class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            // Dragging x axis gizmo
            if (xAxisActive && !yAxisActive) {
                // TODO: 09/03/2022 Fix gizmos
                activeGameObject.scale(-MouseListener.getWorld().x, 0);
                // Dragging x axis gizmo
            } else if (yAxisActive) {
                activeGameObject.scale(0, -MouseListener.getWorld().y);
            }
        }
        super.editorUpdate(dt);
    }
}
