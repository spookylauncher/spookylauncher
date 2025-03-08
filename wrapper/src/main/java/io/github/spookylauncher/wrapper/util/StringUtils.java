package io.github.spookylauncher.wrapper.util;

public final class StringUtils {
    public static int countOf(String str, char character) {
        int count = 0;

        for(int i = 0;i < str.length();i++) {
            if(str.charAt(i) == character) count++;
        }

        return count;
    }
}