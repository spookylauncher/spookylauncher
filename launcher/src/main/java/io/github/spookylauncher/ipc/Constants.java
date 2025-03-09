package io.github.spookylauncher.ipc;

import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.log.Logger;

import java.io.File;

public final class Constants {
    private static String file;
    public static final long FILE_SIZE = 100000L;
    public static final int RECORD_SIZE = 128;

    public static String getFile() {
        return file;
    }

    public static void initializeFile(final File workDir) {
        if(file != null) throw new IllegalStateException("file already initialized");

        final File launcherDir = new File(workDir, "launcher");

        if(!launcherDir.exists() && !launcherDir.mkdirs()) Logger.log(Level.ERROR, "ipc constants", "failed to create launcher directory");

        file = new File(launcherDir, "ipc_mapped_bus.bin").getAbsolutePath();
    }
}