package io.github.spookylauncher.wrapper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;

public final class Json {

    private static final Gson GSON;
    private static final Gson PRETTY_PRINTING_GSON;

    public static String toJson(Object o) {
        return toJson(o, true);
    }

    public static String toJson(Object o, boolean prettyPrinting) {
        return (prettyPrinting ? PRETTY_PRINTING_GSON : GSON).toJson(o);
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) { return GSON.fromJson(reader, clazz); }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return GSON.fromJson(str, clazz);
    }

    static {
        GsonBuilder builder = new GsonBuilder();

        GSON = builder.create();

        builder.setPrettyPrinting();

        PRETTY_PRINTING_GSON = builder.create();
    }
}