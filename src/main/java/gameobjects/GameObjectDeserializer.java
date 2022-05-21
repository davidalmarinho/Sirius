package gameobjects;

import com.google.gson.*;
import gameobjects.components.Component;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray componentList = jsonObject.getAsJsonArray("componentList");

        GameObject go = new GameObject(name);
        for (JsonElement e : componentList) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }

        return go;
    }
}
