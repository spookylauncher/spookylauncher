package io.github.spookylauncher.util.io;

import io.github.spookylauncher.advio.Os;
import io.github.spookylauncher.advio.collectors.Collector;
import io.github.spookylauncher.advio.peddlers.Peddler;
import com.google.gson.*;
import io.github.spookylauncher.tree.GeneralDate;
import io.github.spookylauncher.tree.GsonGeneralDateAdapter;
import io.github.spookylauncher.tree.GsonOsAdapter;

import java.io.IOException;
import java.io.Reader;

public final class Json {

    private static final Gson GSON;
    private static final Gson PRETTY_PRINTING_GSON;

    public static void peddleJson(Peddler peddler, Object obj) throws IOException {
        peddleJson(peddler, obj, true);
    }

    public static void peddleJson(Peddler peddler, Object object, boolean prettyPrinting) throws IOException {
        peddler.peddleString(toJson(object, prettyPrinting));
    }

    public static <T> T collectJson(Collector collector, Class<T> clazz) throws IOException {
        return fromJson(collector.collectString(), clazz);
    }

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
        GsonBuilder builder =
                new GsonBuilder()
                .registerTypeAdapter(GeneralDate.class, new GsonGeneralDateAdapter())
                .registerTypeAdapter(Os.class, new GsonOsAdapter());

        GSON = builder.create();

        builder.setPrettyPrinting();

        PRETTY_PRINTING_GSON = builder.create();
    }
}