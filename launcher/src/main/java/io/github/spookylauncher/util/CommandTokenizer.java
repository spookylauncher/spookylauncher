package io.github.spookylauncher.util;

import java.util.*;

public final class CommandTokenizer {
    public static List<String> tokenize(String cmd) {
        List<String> tokens = new ArrayList<>();

        StringBuilder tokenBuilder = new StringBuilder();

        boolean quotes = false;

        for(int i = 0;i < cmd.length();i++) {
            char c = cmd.charAt(i);

            if(c == '"') quotes = !quotes;
            else if(c == ' ' && !quotes) {
                tokens.add(tokenBuilder.toString());
                tokenBuilder.setLength(0);
            } else tokenBuilder.append(c);
        }

        return tokens;
    }
}