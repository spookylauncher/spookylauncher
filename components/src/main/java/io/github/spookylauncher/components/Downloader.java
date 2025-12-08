package io.github.spookylauncher.components;

import io.github.spookylauncher.io.InstallAdapter;
import io.github.spookylauncher.io.IOUtils;
import io.github.spookylauncher.io.collectors.Collector;
import io.github.spookylauncher.components.ui.UIProvider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public final class Downloader extends LauncherComponent {
    private static final Logger LOG = Logger.getLogger("downloader");

    public Downloader(ComponentsController components) {
        super("Downloader", components);
    }

    public boolean downloadOrUnpack(boolean download, File destination, Collector collector, Options options) {
        if(download) return download(destination, collector, options);
        else return unpackZip(destination, collector, options);
    }

    public boolean download(File destination, Collector collector, Options options) {
        AtomicBoolean canceled = new AtomicBoolean();

        LOG.info("starting downloading of some resource to \"" + destination.getAbsolutePath() + "\"");

        try {
            InstallAdapter adapter = createInstallAdapter(destination, options, canceled);

            if(adapter != null) {
                adapter.consumeFullPaths = options.consumeFullPaths;

                adapter.titleConsumer.accept(String.format(options.subtitleFormat, options.subtitle));

                IOUtils.install(collector, destination, adapter);

                adapter.onEnd.run();
            }
        } catch(Exception e) {
            LOG.severe("downloading failed");
            components.get(ErrorHandler.class).handleException("installationError", e);
        }

        return !canceled.get();
    }

    public boolean unpackZip(File destination, Collector collector, Options options) {
        AtomicBoolean canceled = new AtomicBoolean();

        LOG.info("starting downloading and unpacking some resource to \"" + destination.getAbsolutePath() + "\"");

        try {
            InstallAdapter adapter = createInstallAdapter(destination, options, canceled);

            if(adapter != null) {
                adapter.consumeFullPaths = options.consumeFullPaths;

                IOUtils.unzip(collector, destination, adapter);

                adapter.onEnd.run();
            }
        } catch(Exception e) {
            LOG.severe("downloading and unpacking failed");
            LOG.throwing("io.github.spookylauncher.components.Downloader", "unpackZip", e);
            components.get(ErrorHandler.class).handleException("installationError", e);
        }

        return !canceled.get();
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
                components.get(UIProvider.class).dialogs().createInstallationDialog(
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