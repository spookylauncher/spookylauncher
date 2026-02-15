package io.github.spookylauncher.components;

import io.github.spookylauncher.util.Json;
import io.github.spookylauncher.io.collectors.URLCollector;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManifestDownloader<T> extends LauncherComponent {

    private static final Logger LOG = Logger.getLogger("manifest downloader");

    private final Class<T> manifestClass;
    private final String manifestUrl;
    protected T manifest;

    private HashSet<Supplier<Boolean>> onDownloaded = new HashSet<>();

    public ManifestDownloader<T> addOnDownloadedEvent(Supplier<Boolean> event) {
        onDownloaded.add(event);
        return this;
    }

    public ManifestDownloader(ComponentsController components, Class<T> manifestClass, String manifestUrl) {
        super("Manifest Downloader", components);
        this.manifestClass = manifestClass;
        this.manifestUrl = manifestUrl;
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        downloadManifest();
    }

    public boolean downloadManifest() {
        LOG.info("manifest downloading started (" + manifestUrl + ")");
        try {
            this.manifest = Json.collectJson(new URLCollector(manifestUrl), manifestClass);

            Set<Supplier<Boolean>> toRemove = new HashSet<>();

            for(Supplier<Boolean> event : onDownloaded) {
                if(event.get()) toRemove.add(event);
            }

            onDownloaded.removeAll(toRemove);

            LOG.info("manifest successfully downloaded");

            return true;
        } catch(Exception e) {
            LOG.severe("failed to download manifest \"" + manifestUrl + "\"");
            LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ManifestDownloader", "downloadManifest", "Throw!", e);
            components.get(ErrorHandler.class).handleException("manifestLoadError", e);
            return false;
        }
    }

    public T getManifest() { return this.manifest; }
}