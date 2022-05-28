package sirius.encode_tools;

import com.google.gson.*;
import sirius.animations.Frame;
import sirius.editor.imgui.sprite_animation_window.AnimationBox;

import java.lang.reflect.Type;

public class AnimationBoxDeserializer implements JsonDeserializer<AnimationBox> {

    @Override
    public AnimationBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String trigger = jsonObject.get("trigger").getAsString();
        float x = jsonObject.get("x").getAsFloat();
        float y = jsonObject.get("y").getAsFloat();
        JsonArray frameList = jsonObject.getAsJsonArray("frameList");

        AnimationBox box = new AnimationBox(trigger, x, y);
        for (JsonElement e : frameList) {
            Frame frame = context.deserialize(e, Frame.class);
            box.addFrame(frame);
        }

        return box;
    }
}
