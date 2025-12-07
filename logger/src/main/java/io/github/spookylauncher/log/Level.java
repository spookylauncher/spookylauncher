package io.github.spookylauncher.log;

public enum Level {
    DEBUG("debug"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    FATAL("fatal");

    public final String name;

    Level(final String name) {
        this.name = name;
    }
}