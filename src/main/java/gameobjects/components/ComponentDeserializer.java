package gameobjects.components;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        // Get the component's type
        String type = jsonObject.get("type").getAsString();
        // Gets its properties (variables)
        JsonElement element = jsonObject.get("properties");

        try {
            // Deserializes, extracting its properties and put its "label" on it
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        // Register as type the path of the class
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        // Serialize component but keeps its class name
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}
