package io.github.spookylauncher.wrapper.util.log;

import java.io.PrintStream;

import static java.lang.System.*;

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

    public static void log(final Level level, final String location, final Object obj) {
        if(level == Level.DEBUG && !DEBUG) return;

        final PrintStream pipedOut = (level == Level.ERROR ? err : out);

        pipedOut.print("[" + level.name + "] [" + location + "] : ");

        if(obj instanceof Throwable)
            ((Throwable) obj).printStackTrace(pipedOut);
        else
            pipedOut.println(obj);
    }
}