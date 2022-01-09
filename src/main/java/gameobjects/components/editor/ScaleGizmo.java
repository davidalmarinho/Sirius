package gameobjects.components.editor;

import gameobjects.components.Sprite;
import jade.input.MouseListener;

class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void update(float dt) {
        if (activeGameObject != null) {
            // Dragging x axis gizmo
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= MouseListener.getWorldDeltaX();
                // Dragging x axis gizmo
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getWorldDeltaY();
            }
        }
        super.update(dt);
    }
}
