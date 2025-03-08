package io.github.spookylauncher.util.uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public final class QueryParser {
    private static void put(final Map<String, String> query, final StringBuilder key, final StringBuilder value) {
        try {
            query.put(key.toString(), URLDecoder.decode(value.toString(), "UTF-8"));
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        key.setLength(0);
        value.setLength(0);
    }

    public static Map<String, String> parse(final String rawQuery) {
        final Map<String, String> map = new HashMap<>();

        final StringBuilder keyBuilder = new StringBuilder(), valueBuilder = new StringBuilder();

        boolean valueParsing = false;

        for(int i = 0;i < rawQuery.length();i++) {
            char current = rawQuery.charAt(i);

            if(current == '=') {
                valueParsing = true;
                continue;
            } else if(current == '&') {
                put(map, keyBuilder, valueBuilder);
                continue;
            }

            if(!valueParsing) keyBuilder.append(current);
            else valueBuilder.append(current);
        }

        put(map, keyBuilder, valueBuilder);

        return map;
    }
}