package gameobjects;

import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import sirius.SiriusTheFox;
import sirius.editor.MouseControls;
import sirius.scenes.LevelEditorSceneInitializer;
import sirius.scenes.ISceneInitializer;
import sirius.utils.Settings;
import org.joml.Vector2f;

public class Prefabs {
    // uid to push into ImGui. It is reset in LevelEditorSceneInitializer class.
    public static int uid = 0;

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = SiriusTheFox.getCurrentScene().createGameObject("Sprite_Object_Gen");
        block.setScale(sizeX, sizeY);
        SpriteRenderer spriteRenderer = SpriteRenderer.Builder.newInstance().setSprite(sprite).build();
        block.addComponent(spriteRenderer);

        return block;
    }

    /**
     * Adds a new prefab to ImGui.
     * @param prefabs This interface is a functional interface. Throw this parameter we can parse a
     *                customized method. The goal of this is to generate prefabs with different textures.
     * The path of the spritesheet or of the texture to put in the menu.
     */
    public static void addPrefabImGui(IPrefabs prefabs, Sprite sprite) {
        float spriteWidth = sprite.getWidth() * 2;
        float spriteHeight = sprite.getHeight() * 2;

        int id = sprite.getTextureID();
        Vector2f[] texCoords = sprite.getTextureCoordinates();

        ImGui.pushID(uid++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {

            GameObject object = prefabs.generate(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
            // Attach object to the mouse cursor -- We have to get the LevelEditorStuff game object to accomplish this objective
            ISceneInitializer sceneInitializer = SiriusTheFox.getCurrentScene().getSceneInitializer();
            if (sceneInitializer instanceof LevelEditorSceneInitializer)
                ((LevelEditorSceneInitializer) sceneInitializer).getLevelEditorStuff()
                        .getComponent(MouseControls.class).pickupObject(object);
        }
        ImGui.popID();
    }

    /**
     * Keeps the prefabs in the same line
     */
    public static void sameLine() {
        ImGui.sameLine();
    }
}
