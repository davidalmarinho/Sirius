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
            if (xAxisActive && !yAxisActive)
                activeGameObject.transform.position.x -= MouseListener.getGameViewportDeltaX();
            // Dragging y axis gizmo
            else if (yAxisActive)
                activeGameObject.transform.position.y -= MouseListener.getGameViewportDeltaY();
        }
        super.editorUpdate(dt);
    }
}
