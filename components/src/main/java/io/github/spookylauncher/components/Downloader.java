package io.github.spookylauncher.components;

import io.github.spookylauncher.io.InstallAdapter;
import io.github.spookylauncher.io.IOUtils;
import io.github.spookylauncher.io.collectors.Collector;
import io.github.spookylauncher.components.ui.UIProvider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Downloader extends LauncherComponent {
    private static final Logger LOG = Logger.getLogger("downloader");
    private final Executor executor = Executors.newCachedThreadPool();

    public Downloader(ComponentsController components) {
        super("Downloader", components);
    }

    public interface IDownloadMethod {
        void download(File destination, Collector collector, Options options, Consumer<Boolean> onInstalled);
    }

    public void download(File destination, Collector collector, Options options, Consumer<Boolean> onInstalled) {
        synchronized (Downloader.class) {
            AtomicBoolean canceled = new AtomicBoolean();

            LOG.info("starting downloading of some resource to \"" + destination.getAbsolutePath() + "\"");

            try {
                InstallAdapter adapter = createInstallAdapter(destination, options, canceled);

                if(adapter != null) {
                    adapter.consumeFullPaths = options.consumeFullPaths;

                    adapter.titleConsumer.accept(options.subtitle);

                    executor.execute(() -> {
                        try {
                            IOUtils.install(collector, destination, adapter);
                        } catch (IOException e) {
                            LOG.severe("downloading failed");
                            LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.Downloader", "download", "THROW", e);
                        }

                        adapter.onEnd.run();

                        onInstalled.accept(!canceled.get());
                    });
                }
            } catch(Exception e) {
                LOG.severe("downloading failed");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.Downloader", "download", "THROW", e);
                components.get(ErrorHandler.class).handleException("installationError", e);
            }
        }
    }

    public void downloadAndUnpackZip(File destination, Collector collector, Options options, Consumer<Boolean> onInstalled) {
        synchronized (Downloader.class) {
            AtomicBoolean canceled = new AtomicBoolean();

            LOG.info("starting downloading and unpacking some resource to \"" + destination.getAbsolutePath() + "\"");

            try {
                InstallAdapter adapter = createInstallAdapter(destination, options, canceled);

                if (adapter != null) {
                    adapter.consumeFullPaths = options.consumeFullPaths;

                    executor.execute(() -> {
                        try {
                            IOUtils.unzip(collector, destination, adapter);
                        } catch (IOException e) {
                            LOG.severe("downloading failed");
                            LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.Downloader", "download", "THROW", e);
                        }

                        adapter.onEnd.run();

                        onInstalled.accept(!canceled.get());
                    });
                }
            } catch (Exception e) {
                LOG.severe("downloading and unpacking failed");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.Downloader", "download", "THROW", e);
                components.get(ErrorHandler.class).handleException("installationError", e);
            }
        }
    }

    private InstallAdapter createInstallAdapter(File destination, Options options, AtomicBoolean canceled) {
        Runnable onCancel = null;

        InstallAdapter inputAdapter;

        if(options.adapter != null) {
            inputAdapter = options.adapter;
            onCancel = inputAdapter.onCancel;
        } else inputAdapter = new InstallAdapter();

        Runnable finalOnCancel = onCancel;

        inputAdapter.onCancel = () -> {
            canceled.set(true);

            if(options.deleteOnCancel) {
                try {
                    IOUtils.deleteTree(destination);
                } catch (IOException e) {
                    LOG.severe("failed to delete tree \"" + destination.getAbsolutePath() + "\"");
                    LOG.throwing("io.github.spookylauncher.components.Downloader", "createInstallAdapter", e);
                }
            }

            if(finalOnCancel != null) finalOnCancel.run();
        };

        return options.ui ?
                components.get(UIProvider.class).dialogs().createInstallationView(
                        options.title,
                        options.subtitleFormat,
                        inputAdapter
                ) : null;
    }

    public static final class Options {
        public String title, subtitleFormat;
        public boolean ui = true;
        public boolean consumeFullPaths = true;
        public boolean deleteOnCancel = true;
        public String subtitle;
        public InstallAdapter adapter;
    }
}