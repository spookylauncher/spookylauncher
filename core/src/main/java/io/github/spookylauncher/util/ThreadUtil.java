package io.github.spookylauncher.util;

public final class ThreadUtil {

    public static Thread runDaemon(Runnable runnable) {
        return run(runnable, true);
    }

    public static Thread run(Runnable runnable) {
        return run(runnable, false);
    }

    public static Thread run(Runnable runnable, boolean daemon) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(daemon);
        thread.start();
        return thread;
    }
}
