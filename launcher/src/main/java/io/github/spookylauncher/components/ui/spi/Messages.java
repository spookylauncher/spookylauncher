package io.github.spookylauncher.components.ui.spi;

public interface Messages {
    default void warning(String title, String message) {
        message(title, message, MessageType.WARNING);
    }

    default void info(String title, String message) {
        message(title, message, MessageType.INFO);
    }

    default void error(String title, String message) {
        message(title, message, MessageType.ERROR);
    }

    void message(String title, String message, MessageType type);
}