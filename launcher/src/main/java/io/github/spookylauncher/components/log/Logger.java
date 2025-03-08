package io.github.spookylauncher.components.log;

import io.github.spookylauncher.util.io.PipedPrintStream;

public final class Logger {
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("SPOOKYDEV"));

    public static final PipedPrintStream out = PipedPrintStream.from(System.out), err = PipedPrintStream.from(System.err);

    public static void throwing(final Throwable t) {
        throwing(t, "unknown");
    }

    public static void log(final Level level, final String msg) {
        log(level, "unknown", msg);
    }

    public static void throwing(final Throwable t, final String location) {
        log(Level.ERROR, location, t.toString());
    }

    public static void log(final Level level, final String location, final Object msg) {
        if(level == Level.DEBUG && !DEBUG) return;

        (level == Level.ERROR ? err : out).println("[" + level.name + "] [" + location + "] : " + msg);
    }
}