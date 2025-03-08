package io.github.spookylauncher.wrapper.util.log;

public final class Logger {
    private static final boolean DEBUG = true;

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

        (level == Level.ERROR || level == Level.FATAL ? System.err : System.out).println("[" + level.name + "] [" + location + "] : " + msg);
    }
}