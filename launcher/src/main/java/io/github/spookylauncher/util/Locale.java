package io.github.spookylauncher.util;

import java.util.Properties;

public final class Locale {
    private final Properties props;
    private final String language;

    public Locale(String language, Properties props) {
        this.language = language;
        this.props = props;
    }

    public String getLanguage() {
        return language;
    }

    public String get(String key) {
        String value;

        if(!props.containsKey(key)) value = key;
        else value = props.getProperty(key);

        if(value != null && value.length() > 0) {
            char[] chars = value.toCharArray();

            chars[0] = Character.toUpperCase(chars[0]);

            value = new String(chars);
        }

        return value;
    }
}