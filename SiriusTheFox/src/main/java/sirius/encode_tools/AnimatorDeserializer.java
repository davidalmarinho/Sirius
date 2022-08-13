package sirius.encode_tools;

import com.google.gson.*;
import sirius.editor.imgui.sprite_animation_window.AnimationBlueprint;
import sirius.editor.imgui.sprite_animation_window.AnimationBox;
import sirius.editor.imgui.sprite_animation_window.Wire;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AnimatorDeserializer implements JsonDeserializer<AnimationBlueprint> {

    @Override
    public AnimationBlueprint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray pointsJsonArray = jsonObject.getAsJsonArray("wireList");
        JsonArray animationBoxesJsonArray = jsonObject.getAsJsonArray("animationBoxList");

        List<Wire> wireList = new ArrayList<>();
        List<AnimationBox> animationBoxList = new ArrayList<>();

        for (JsonElement e : pointsJsonArray)
            wireList.add(context.deserialize(e, Wire.class));

        for (JsonElement e : animationBoxesJsonArray)
            animationBoxList.add(context.deserialize(e, AnimationBox.class));

        return new AnimationBlueprint(wireList, animationBoxList);
    }
}
