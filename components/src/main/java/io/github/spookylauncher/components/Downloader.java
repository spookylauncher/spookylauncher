package io.github.spookylauncher.components;

import io.github.spookylauncher.io.InstallAdapter;
import io.github.spookylauncher.io.IOUtils;
import io.github.spookylauncher.io.collectors.Collector;
import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.components.ui.spi.UIProvider;
import io.github.spookylauncher.log.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Downloader extends LauncherComponent {
    public Downloader(ComponentsController components) {
        super("Downloader", components);
    }

    public boolean downloadOrUnpack(boolean download, File destination, Collector collector, Options options) {
        if(download) return download(destination, collector, options);
        else return unpackZip(destination, collector, options);
    }

    public boolean download(File destination, Collector collector, Options options) {
        AtomicBoolean canceled = new AtomicBoolean();

        log(Level.INFO, "starting downloading of some resource to \"" + destination.getAbsolutePath() + "\"");

        try {
            InstallAdapter adapter = createInstallAdapter(destination, options, canceled);

            if(adapter != null) {
                adapter.consumeFullPaths = options.consumeFullPaths;

                adapter.titleConsumer.accept(String.format(options.subtitleFormat, options.subtitle));

                IOUtils.install(collector, destination, adapter);

                adapter.onEnd.run();
            }
        } catch(Exception e) {
            log(Level.ERROR, "downloading failed");
            components.get(ErrorHandler.class).handleException("installationError", e);
        }

        return !canceled.get();
    }

    public boolean unpackZip(File destination, Collector collector, Options options) {
        AtomicBoolean canceled = new AtomicBoolean();

        log(Level.INFO, "starting downloading and unpacking some resource to \"" + destination.getAbsolutePath() + "\"");

        try {
            InstallAdapter adapter = createInstallAdapter(destination, options, canceled);

            if(adapter != null) {
                adapter.consumeFullPaths = options.consumeFullPaths;

                IOUtils.unzip(collector, destination, adapter);

                adapter.onEnd.run();
            }
        } catch(Exception e) {
            log(Level.ERROR, "downloading and unpacking failed");
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
                    Logger.log(Level.ERROR, "downloader", "failed to delete tree \"" + destination.getAbsolutePath() + "\"");
                    Logger.log(Level.ERROR, "downloader", e);
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