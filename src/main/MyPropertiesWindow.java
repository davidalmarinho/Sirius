package main;

import components.BreakableBrick;
import components.PlayerController;
import gameobjects.GameObject;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.editor.imgui.PropertiesWindow;

public class MyPropertiesWindow implements ICustomPropertiesWindow {

    @Override
    public void imgui(GameObject go) {
        PropertiesWindow.addMenuItem(go, "Add Player Controller", new PlayerController());
        PropertiesWindow.addMenuItem(go, "Add BreakableBrick", new BreakableBrick());
    }
}
