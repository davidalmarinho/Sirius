package sirius.editor.imgui.sprite_animation_window;

import java.util.ArrayList;
import java.util.List;

public class AnimationBlueprint {
    public List<Point> pointList;
    public List<AnimationBox> animationBoxList;

    public AnimationBlueprint() {
        this.animationBoxList = new ArrayList<>();
        this.pointList        = new ArrayList<>();
    }

    public AnimationBlueprint(List<Point> pointList, List<AnimationBox> animationBoxList) {
        this.pointList = new ArrayList<>(pointList);

        this.animationBoxList = new ArrayList<>();
        for (AnimationBox animationBox : animationBoxList) {
            this.animationBoxList.add(new AnimationBox(animationBox));
        }
    }
}
