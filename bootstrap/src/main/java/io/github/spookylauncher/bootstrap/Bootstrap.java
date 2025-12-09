package io.github.spookylauncher.bootstrap;

import io.github.spookylauncher.io.IOUtils;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.events.EventsManager;
import io.github.spookylauncher.components.events.Events;
import io.github.spookylauncher.protocol.ProtocolRequestHandler;
import io.github.spookylauncher.protocol.ProtocolSender;
import io.github.spookylauncher.components.ComponentsRegister;
import io.github.spookylauncher.ipc.Constants;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.LogFormatter;

import javax.swing.*;
import java.io.*;
import java.nio.channels.FileLock;
import java.util.function.Consumer;
import java.util.logging.*;

public final class Bootstrap implements Runnable {
    private static final String UI_IMPLEMENTATION = "io.github.spookylauncher.components.ui.swing.SwingUIProvider";
    private static final Logger LOG = Logger.getLogger("bootstrap");

    public final File workDirectory;
    private final Consumer<ComponentsController> protocolConsumer;
    private final FileLock lock;
    private final boolean hasActiveInstance;

    public static void main(String[] args) throws IOException {
        Logger rootLogger = Logger.getLogger("");

        for(Handler handler : rootLogger.getHandlers())
            rootLogger.removeHandler(handler);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new LogFormatter());

        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.INFO);

        final File workDir = new File(System.getenv("APPDATA"), ".spookylauncher");

        Constants.initializeFile(workDir);

        final File launcherInternalDir = new File(workDir, "launcher");

        if(!launcherInternalDir.exists() && !launcherInternalDir.mkdirs()) {
            LOG.severe("failed to create internal launcher directory (" + launcherInternalDir.getAbsolutePath() + ")");
            System.exit(1);
            return;
        }

        final File lockFile = new File(launcherInternalDir, "lock");

        final boolean hasActiveInstance;

        if(!lockFile.exists()) {
            lockFile.createNewFile();
            hasActiveInstance = false;
        } else hasActiveInstance = IOUtils.isLocked(lockFile);

        if(hasActiveInstance) LOG.info("detected another launcher instance");

        Consumer<ComponentsController> protocolConsumer = null;

        if(args.length > 0 && args[0].equalsIgnoreCase("--viaprotocol")) {
            if(args.length < 2 || args[1].isEmpty()) {
                LOG.severe("specified \"--viaprotocol\" but URI is missing");
                return;
            }

            try {
                protocolConsumer = ProtocolRequestHandler.createConsumer(args[1], hasActiveInstance);

                if(hasActiveInstance) {
                    protocolConsumer.accept(null);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        new Bootstrap(workDir, protocolConsumer, hasActiveInstance, hasActiveInstance ? null : IOUtils.lock(lockFile)).run();
    }

    public Bootstrap(final File workDirectory, final Consumer<ComponentsController> protocolConsumer, final boolean hasActiveInstance, final FileLock lock) {
        this.workDirectory = workDirectory;
        this.protocolConsumer = protocolConsumer;
        this.hasActiveInstance = hasActiveInstance;
        this.lock = lock;
    }

    @Override
    public void run() {
        final boolean workDirectoryCreationFailed = !this.workDirectory.exists() && !this.workDirectory.mkdirs();

        final ComponentsController controller = new ComponentsController();

        final ComponentsRegister reg = new ComponentsRegister(controller, this.workDirectory);

        reg.createComponents(UI_IMPLEMENTATION);

        if(this.protocolConsumer != null) this.protocolConsumer.accept(controller);

        LOG.info("current working directory: \"" + this.workDirectory.getAbsolutePath() + "\"");

        //log(INFO, LOG_ID, "current device id: " + StringUtils.toHex(Identifier.getMACHash()));

        if(workDirectoryCreationFailed) LOG.info("failed to create work directory!");

        if(protocolConsumer == null && this.hasActiveInstance) {
            Translator translator = controller.get(Translator.class);

            controller.addOnInitializedEvent(controller.getIndex(translator), (c) -> {
                Locale locale = translator.getLocale();

                try {
                    ProtocolSender sender = new ProtocolSender();

                    sender.open();

                    sender.sendFrameTopFront();

                    sender.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, locale.get("launcherAlreadyRunning"), locale.get("startError"), JOptionPane.WARNING_MESSAGE);

                System.exit(0);

                return true;
            });
        }

        controller.get(EventsManager.class).subscribe(Events.SHUTDOWN, args -> {
            LOG.info("releasing file lock");
            try {
                lock.release();
                lock.channel().close();
            } catch(Exception e) {
                LOG.severe("failed to release file lock");
            }
        });

        reg.initializeComponents();
    }

}