package sirius.editor.imgui;

import gameobjects.GameObject;
import gameobjects.components.Sprite;

@FunctionalInterface
public interface IPrefabs {
    GameObject generate(Sprite sprite, float sizeX, float sizeY);
}
