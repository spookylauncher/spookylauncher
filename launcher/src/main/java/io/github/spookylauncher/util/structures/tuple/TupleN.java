package io.github.spookylauncher.util.structures.tuple;

import java.util.*;

public final class TupleN {
    private Map<String, Object> objects = new HashMap<>();

    public TupleN(Tuple2<String, ?>... values) {
        for(Tuple2<String, ?> value : values) objects.put(value.x, value.y);
    }

    public <T> void set(String key, T obj) {
        objects.put(key, obj);
    }

    public <T> T getUnchecked(String key) {
        return get(key, null);
    }

    public <T> T get(String key, Class<? extends T> clazz) {
        Object obj = objects.get(key);

        if(obj == null) return null;
        else {
            if(!clazz.isAssignableFrom(obj.getClass())) return null;
            else return (T) obj;
        }
    }
}