package io.github.spookylauncher.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public final class GsonGeneralDateAdapter implements JsonSerializer<GeneralDate>, JsonDeserializer<GeneralDate> {

    public JsonElement serialize(GeneralDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.day + "/" + src.month + "/" + src.day);
    }

    public GeneralDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        String[] splits = json.getAsString().split("/");

        return new GeneralDate
                (
                        Integer.parseInt(splits[0]),
                        Integer.parseInt(splits[1]),
                        Integer.parseInt(splits[2])
                );
    }
}