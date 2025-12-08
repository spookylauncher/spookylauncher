package io.github.spookylauncher.ipc;

import java.io.File;
import java.util.logging.Logger;

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

        if(!launcherDir.exists() && !launcherDir.mkdirs()) Logger.getLogger("ipc constants").severe("failed to create launcher directory");

        file = new File(launcherDir, "ipc_mapped_bus.bin").getAbsolutePath();
    }
}