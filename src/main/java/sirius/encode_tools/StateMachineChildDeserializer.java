package sirius.encode_tools;

import com.google.gson.*;
import sirius.editor.imgui.sprite_animation_window.AnimationBox;
import sirius.editor.imgui.sprite_animation_window.Point;
import sirius.editor.imgui.sprite_animation_window.StateMachineChild;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StateMachineChildDeserializer implements JsonDeserializer<StateMachineChild> {

    @Override
    public StateMachineChild deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray pointsJsonArray = jsonObject.getAsJsonArray("pointList");
        JsonArray animationBoxesJsonArray = jsonObject.getAsJsonArray("animationBoxList");

        List<Point> pointList = new ArrayList<>();
        List<AnimationBox> animationBoxList = new ArrayList<>();

        for (JsonElement e : pointsJsonArray)
            pointList.add(context.deserialize(e, Point.class));

        for (JsonElement e : animationBoxesJsonArray)
            animationBoxList.add(context.deserialize(e, AnimationBox.class));

        return new StateMachineChild(pointList, animationBoxList);
    }
}
