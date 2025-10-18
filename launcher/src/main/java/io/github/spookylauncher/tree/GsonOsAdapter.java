package io.github.spookylauncher.tree;

import io.github.spookylauncher.advio.OSType;
import com.google.gson.*;

import java.lang.reflect.Type;

public final class GsonOsAdapter implements JsonSerializer<OSType>, JsonDeserializer<OSType> {

    public JsonElement serialize(OSType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name.toLowerCase());
    }

    public OSType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The OS should be a string value");
        }

        String name = json.getAsString();

        for(OSType osType : OSType.values()) {
            if(osType.name.equalsIgnoreCase(name)) return osType;
        }

        return null;
    }
}