package jade.editor;

import gameobjects.components.Component;

/**
 * Game objects that are been holding using mouse cursor will have this component until the game object
 * is placed.
 * This was created for management purposes, like not saving game objects that have this component (so, doesn't
 * save, for example, a game object that wasn't placed but is been holding by the mouse).
 * It is also used to mark game objects, because sometimes there are game object that can't be deleted in the
 * current frame but all others game objects have to be saved. So, to don't save the unintended game objects,
 * they are also marked with this component.
 */
public class NonPickable extends Component {

}
