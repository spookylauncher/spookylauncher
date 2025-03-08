package io.github.spookylauncher.components.events;

public interface Handler extends Runnable {
    @Override
    default void run() {
        invoke();
    }

    void invoke(Object...args);
}