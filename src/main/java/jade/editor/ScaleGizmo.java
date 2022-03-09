package jade.editor;

import gameobjects.components.Sprite;
import jade.input.MouseListener;

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
                activeGameObject.transform.scale.x -= MouseListener.getWorld().x;
                // Dragging x axis gizmo
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getWorld().y;
            }
        }
        super.editorUpdate(dt);
    }
}
