package sirius.editor.imgui;

import imgui.ImGui;
import sirius.SiriusTheFox;
import sirius.Sound;
import sirius.editor.MouseControls;
import sirius.rendering.spritesheet.Spritesheet;
import sirius.scenes.ISceneInitializer;
import sirius.scenes.LevelEditorSceneInitializer;
import sirius.utils.AssetPool;

import java.io.File;
import java.util.Collection;

public class TabBar {
    private Spritesheet sprites;
    public boolean show = true;

    public void imgui() {
        if (!show)
            return;

        ImGui.begin("Objects");

        if (ImGui.beginTabBar("TabBar")) {
            if (ImGui.beginTabItem("Icons")) {
                if (JImGui.spritesLayout(sprites)) {
                    // Attach object to the mouse cursor -- We have to get the LevelEditorStuff game object to accomplish this objective
                    ISceneInitializer sceneInitializer = SiriusTheFox.getCurrentScene().getSceneInitializer();
                    if (sceneInitializer instanceof LevelEditorSceneInitializer)
                        ((LevelEditorSceneInitializer) sceneInitializer).getLevelEditorStuff()
                                .getComponent(MouseControls.class).pickupObject(JImGui.getSelectedGameObject());
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Prefabs")) {
                // Check if we have customized prefabs
                ICustomPrefabs customPrefabs = SiriusTheFox.getWindow().getICustomPrefabs();
                if (customPrefabs != null) {
                    customPrefabs.imgui();
                }
                Prefabs.uid = 0;
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilePath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying())
                            sound.play();
                        else
                            sound.stop();
                    }
                }

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }

    public void loadSpritesheet(Spritesheet sprites) {
        this.sprites = sprites;
    }
}
