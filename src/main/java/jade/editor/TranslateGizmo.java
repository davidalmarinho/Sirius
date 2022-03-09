package jade.editor;

import gameobjects.components.Sprite;
import jade.input.MouseListener;

class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            // Dragging x axis gizmo
            if (xAxisActive && !yAxisActive) {
                // TODO: 09/03/2022 Fix gizmos
                activeGameObject.transform.position.x -= MouseListener.getWorld().x;
            // Dragging y axis gizmo
            } else if (yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorld().y;
            }
        }
        super.editorUpdate(dt);
    }
}
