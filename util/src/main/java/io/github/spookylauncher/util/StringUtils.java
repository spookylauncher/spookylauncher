package io.github.spookylauncher.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.function.*;

public final class StringUtils {
    public static final String ASCII = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static String valueOf(Object o) {
        if(o instanceof Properties) return valueOf((Properties) o);
        else if(o instanceof Map) return valueOf((Map<?, ?>) o);
        else return String.valueOf(o);
    }
    public static String valueOf(final Properties props) {
        final StringBuilder builder = new StringBuilder();

        for(final String key : props.stringPropertyNames()) {
            builder.append(key);
            builder.append('=');
            builder.append(props.getProperty(key));
            builder.append(", ");
        }

        final int len = builder.length();

        if(len > 0) builder.setLength(len - 2);

        return builder.toString();
    }
    public static String valueOf(final Map<?, ?> map) {
        final StringBuilder builder = new StringBuilder();

        for(final Map.Entry<?, ?> e : map.entrySet()) {
            builder.append(valueOf(e.getKey()));
            builder.append('=');
            builder.append(valueOf(e.getValue()));
            builder.append(", ");
        }

        final int len = builder.length();

        if(len > 0) builder.setLength(len - 2);

        return builder.toString();
    }

    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static boolean isASCIIString(String str) {
        if(str.isEmpty()) return false;

        for(char c : str.toCharArray()) {
            if(ASCII.indexOf(c) == -1) return false;
        }

        return true;
    }

    public static String getStackTrace(Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PrintWriter writer = new PrintWriter(baos);

        t.printStackTrace(writer);

        writer.close();

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public static void forEach(String str, Consumer<Character> action) {
        forEach(str, character -> {
            action.accept(character);
            return false;
        });
    }

    public static void forEach(String str, Function<Character, Boolean> action) {
        for(int i = 0;i < str.length();i++) {
            if(action.apply(str.charAt(i))) break;
        }
    }

    public static int find(String str, Predicate<Character> checker) {
        for(int i = 0;i < str.length();i++) {
            if(checker.test(str.charAt(i))) return i;
        }

        return -1;
    }
}