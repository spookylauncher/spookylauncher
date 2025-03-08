package io.github.spookylauncher.tree;

import io.github.spookylauncher.advio.Os;
import com.google.gson.*;

import java.lang.reflect.Type;

public final class GsonOsAdapter implements JsonSerializer<Os>, JsonDeserializer<Os> {

    public JsonElement serialize(Os src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name.toLowerCase());
    }

    public Os deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The OS should be a string value");
        }

        String name = json.getAsString();

        for(Os os : Os.values()) {
            if(os.name.equalsIgnoreCase(name)) return os;
        }

        return null;
    }
}