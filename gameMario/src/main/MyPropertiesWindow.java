package main;

import components.BreakableBrick;
import components.Ground;
import components.PlayerController;
import gameobjects.GameObject;
import jade.editor.ICustomPropertiesWindow;
import jade.editor.PropertiesWindow;

public class MyPropertiesWindow implements ICustomPropertiesWindow {

    @Override
    public void imgui(GameObject go) {
        PropertiesWindow.addMenuItem(go, "Add Player Controller", new PlayerController());
        PropertiesWindow.addMenuItem(go, "Add Ground", new Ground());
        PropertiesWindow.addMenuItem(go, "Add BreakableBrick", new BreakableBrick());
    }
}
