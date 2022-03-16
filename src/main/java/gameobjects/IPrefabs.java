package gameobjects;

import gameobjects.components.Sprite;

@FunctionalInterface
public interface IPrefabs {
    GameObject generate(Sprite sprite, float sizeX, float sizeY);
}
