package io.github.spookylauncher.components;

import io.github.spookylauncher.util.Json;
import io.github.spookylauncher.io.collectors.URLCollector;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import static io.github.spookylauncher.log.Level.*;

public class ManifestDownloader<T> extends LauncherComponent {

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
        log(INFO, "manifest downloading started (" + manifestUrl + ")");
        try {
            this.manifest = Json.collectJson(new URLCollector(manifestUrl), manifestClass);

            Set<Supplier<Boolean>> toRemove = new HashSet<>();

            for(Supplier<Boolean> event : onDownloaded) {
                if(event.get()) toRemove.add(event);
            }

            onDownloaded.removeAll(toRemove);

            log(INFO, "manifest successfully downloaded");

            return true;
        } catch(Exception e) {
            log(ERROR, "failed to download manifest \"" + manifestUrl + "\"");
            components.get(ErrorHandler.class).handleException("manifestLoadError", e);
            return false;
        }
    }

    public T getManifest() { return this.manifest; }
}