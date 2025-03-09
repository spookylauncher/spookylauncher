package io.github.spookylauncher.components;

import io.github.spookylauncher.components.ui.spi.UIProvider;
import io.github.spookylauncher.tree.LibrariesCollection;
import io.github.spookylauncher.tree.LibraryInfo;
import io.github.spookylauncher.tree.versions.LibrariesManifest;
import io.github.spookylauncher.advio.AsyncOperation;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.advio.Os;
import io.github.spookylauncher.advio.collectors.URLCollector;
import io.github.spookylauncher.log.Level;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class LibrariesController extends LauncherComponent {

    private final File libsDir;
    private final String manifestDownloaderName;
    private HashMap<String, File> libsDirs = new HashMap<>();

    public LibrariesController(ComponentsController components, File libsDir, String manifestDownloaderName) {
        super("Libraries Controller", components);
        this.libsDir = libsDir;
        this.manifestDownloaderName = manifestDownloaderName;
    }

    public File getLibrariesDirectory() {
        return libsDir;
    }

    private LibrariesManifest getManifest() {
        return ( (ManifestDownloader<LibrariesManifest>) components.get(manifestDownloaderName)).getManifest();
    }

    public File getLibraryFile(LibraryInfo lib) {
        if(!libsDirs.containsKey(lib.name)) {
            libsDirs.put
            (
                lib.name,
                new File
                (
                        libsDir,
                        lib.packet.replace(".", "/") + "/" + lib.name + "/" + lib.version + "/" + lib.name + "-" + lib.version + (lib.isNative ? "" : ".jar")
                )
            );
        }

        return libsDirs.get(lib.name);
    }

    public boolean isInstalled(LibraryInfo library) {
        return getLibraryFile(library).exists();
    }

    public boolean isInstalled(LibrariesCollection collection) {
        if(collection.libraries.length == 0 && collection.collections.length == 0) return false;

        LibrariesManifest manifest = getManifest();

        if(collection.libraries.length != 0) { for(LibraryInfo lib : collection.libraries) if(!isInstalled(manifest.getLibrary(lib.name, lib.version))) return false; }

        if(collection.collections.length != 0) { for(String col : collection.collections) if(!isInstalled(manifest.getLibsCollection(col))) return false; }

        return true;
    }

    public void install(LibrariesCollection collection, Consumer<Boolean> onInstalled) {
        log(Level.INFO, "start installing libraries collection \"" + collection.name + "\"");

        LibrariesManifest manifest = getManifest();

        for(String subCollectionName : collection.collections) {
            LibrariesCollection subCollection = manifest.getLibsCollection(subCollectionName);

            AtomicBoolean failed = new AtomicBoolean();

            install
            (
               subCollection,
               success -> failed.set(!success)
            );

            if(failed.get()) {
                onInstalled.accept(false);
                return;
            }
        }

        for(LibraryInfo lib : collection.libraries) {
            LibraryInfo libInfo = manifest.getLibrary(lib.name, lib.version);

            assert libInfo != null;

            AtomicBoolean failed = new AtomicBoolean();

            install
            (
                libInfo,
                success -> failed.set(!success)
            );

            if(failed.get()) {
                onInstalled.accept(false);
                return;
            }
        }

        if(onInstalled != null) onInstalled.accept(true);
    }

    public void install(LibraryInfo lib, Consumer<Boolean> onInstalled) {
        log(Level.INFO, "started library installing, name: \"" + lib.name + "\", version: " + lib.version);

        final Locale locale = components.get(Translator.class).getLocale();
        final UIProvider uiProvider = components.get(UIProvider.class);

        String url;

        if((url = lib.jar.getDownloadUrl()) == null) {
            log(Level.ERROR, "failed to install library because download is not available");
            uiProvider.messages().error(
                    locale.get("installationError"),
                    String.format(locale.get("libDownloadNotFounded"), lib.name)
            );
            return;
        }

        AsyncOperation.run(
                () -> {
                    assert Os.CURRENT != null;

                    File destination = getLibraryFile(lib);

                    if(!isInstalled(lib)) {

                        if(lib.isNative) {
                            destination.mkdirs();
                        } else {
                            File parent = destination.getParentFile();

                            if(parent != null) parent.mkdirs();
                        }

                        uiProvider.panel().setEnabledButtons(false);

                        Downloader.Options options = new Downloader.Options();

                        options.title = String.format(locale.get("libInstallation"), lib.name);
                        options.subtitleFormat = locale.get("downloadingFormat");
                        options.subtitle = lib.name + "-" + lib.version;
                        options.consumeFullPaths = false;

                        Downloader downloader = components.get(Downloader.class);

                        URLCollector collector = new URLCollector(url);

                        boolean success =
                        downloader.downloadOrUnpack
                                (
                                        !lib.isNative,
                                        destination,
                                        collector,
                                        options
                                );

                        uiProvider.panel().setEnabledButtons(true);

                        if(!success) {
                            log(Level.INFO, "library installation canceled by user");
                            uiProvider.messages().info(
                                    locale.get("libInstallationFailed"),
                                    locale.get("operationCanceledByUser")
                            );
                        } else log(Level.INFO, "library successfully installed");

                        onInstalled.accept(success);
                    }
                }
        );
    }
}