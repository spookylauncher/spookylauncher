package io.github.spookylauncher.protocol;

public enum CommandType {
    SELECT_VERSION("select_version");

    public final String name;

    CommandType(final String name) {
        this.name = name;
    }

    public static CommandType parse(String command) {
        for(CommandType type : values()) {
            if(type.name.equals(command)) return type;
        }

        return null;
    }
}