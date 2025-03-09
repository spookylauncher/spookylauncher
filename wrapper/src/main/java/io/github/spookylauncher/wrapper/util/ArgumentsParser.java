package io.github.spookylauncher.wrapper.util;

import java.util.Properties;

public final class ArgumentsParser {
    public static Properties parseArguments(String[] args) {
        Properties props = new Properties();

        boolean nextValue = false;

        for(int i = 0;i < args.length;i++) {
            boolean prefix = args[i].startsWith("--");

            if(nextValue) {
                final String key = args[i - 1].substring(2);

                if(prefix) props.setProperty(key, "true");
                else {
                    props.setProperty(key, args[i]);
                    nextValue = false;
                }
            } else if(!prefix) throw new IllegalArgumentException("key without prefix: " + args[i]);
            else nextValue = true;
        }

        return props;
    }
}