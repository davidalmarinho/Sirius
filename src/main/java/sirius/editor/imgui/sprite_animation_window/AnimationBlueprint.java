package sirius.editor.imgui.sprite_animation_window;

import java.util.ArrayList;
import java.util.List;

public class AnimationBlueprint {
    public List<Wire> wireList;
    public List<AnimationBox> animationBoxList;

    public AnimationBlueprint() {
        this.animationBoxList = new ArrayList<>();
        this.wireList         = new ArrayList<>();
    }

    public AnimationBlueprint(List<Wire> wireList, List<AnimationBox> animationBoxList) {
        this.wireList = new ArrayList<>();
        for (Wire wire : wireList) {
            this.wireList.add(new Wire(wire));
        }

        this.animationBoxList = new ArrayList<>();
        for (AnimationBox animationBox : animationBoxList) {
            this.animationBoxList.add(new AnimationBox(animationBox));
        }
    }

    public Wire getLastWire() {
        return wireList.get(wireList.size() - 1);
    }

    public void removeLastWire() {
        wireList.remove(wireList.size() - 1);
    }
}
