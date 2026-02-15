package io.github.spookylauncher.components;

import io.github.spookylauncher.components.ui.TitlePanel;
import io.github.spookylauncher.components.ui.UIProvider;
import io.github.spookylauncher.tree.LibrariesCollection;
import io.github.spookylauncher.tree.LibraryInfo;
import io.github.spookylauncher.tree.versions.LibrariesManifest;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.io.collectors.URLCollector;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.StringUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VersionsInstaller extends LauncherComponent {

    private static final Logger LOG = Logger.getLogger("versions installer");

    private final String librariesManifestDownloaderName;
    private final String mirrorsManifestDownloaderName;

    public VersionsInstaller(
            ComponentsController components,
            String librariesManifestDownloaderName,
            String mirrorsManifestDownloaderName
    ) {
        super("Versions Installer", components);
        this.librariesManifestDownloaderName = librariesManifestDownloaderName;
        this.mirrorsManifestDownloaderName = mirrorsManifestDownloaderName;
    }

    public void installLibraries(VersionInfo version, Consumer<Boolean> onInstalledCallback) {
        LibrariesController libsController = components.get(LibrariesController.class);
        LibrariesManifest manifest = ( (ManifestDownloader<LibrariesManifest>) components.get(librariesManifestDownloaderName)).getManifest();

        AtomicInteger uninstalledCount = new AtomicInteger(version.libsCollections.length);

        AtomicBoolean success = new AtomicBoolean(true);

        for(String libs : version.libsCollections) {
            LibrariesCollection libsCollection = manifest.getLibsCollection(libs);

            if(libsCollection == null) continue;

            if(!libsController.isInstalled(libsCollection)) {
                libsController.install(libsCollection,
                        s -> {
                            uninstalledCount.getAndDecrement();

                            if(!s)
                                success.set(false);
                        }
                );
            } else uninstalledCount.getAndDecrement();
        }

        if(onInstalledCallback != null) {
            new Thread(
                    () -> {
                        while(true) {
                            if(uninstalledCount.get() <= 0) {
                                onInstalledCallback.accept(success.get());
                                break;
                            }

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                LOG.logp(Level.WARNING, "io.github.spookylauncher.components.VersionsInstaller", "installLibraries", "THROW", e);
                            }
                        }
                    }
            ).start();
        }
    }

    public void install(VersionInfo version) {
        LOG.info("start installing version: " + version.name);

        final Locale locale = components.get(Translator.class).getLocale();
        final VersionsList versions = components.get(VersionsList.class);
        final UIProvider uiProvider = components.get(UIProvider.class);

        if(versions.isInstalled(version)) {
            LOG.info("installation canceled because version already installed");
            uiProvider.messages().warning(locale.get("installationError"), locale.get("versionAlreadyInstalled"));
            return;
        }

        LibrariesController libsController = components.get(LibrariesController.class);
        LibrariesManifest manifest = ( (ManifestDownloader<LibrariesManifest>) components.get(librariesManifestDownloaderName)).getManifest();

        LOG.info("installing version dependencies");

        if(version.libraries != null) {
            for(LibraryInfo library : version.libraries) {
                LibraryInfo libraryInfo = manifest.getLibrary(library.name, library.version);

                if(libraryInfo == null) continue;

                if(!libsController.isInstalled(libraryInfo)) libsController.install(libraryInfo, null);
            }
        }

        if(version.libsCollections != null)
            installLibraries(version, null);

        File versionDir = new File(versions.versionsDirectory, version.name);

        if(!versionDir.mkdirs()) {
            LOG.severe("failed to create version directory");
            uiProvider.messages().warning(locale.get("installationError"), locale.get("versionDirectoryCreationFailed"));
            return;
        }

        LOG.info("start downloading");

        uiProvider.panel().setEnabledButtons(false);

        Downloader.Options options = new Downloader.Options();

        options.title = locale.get("versionInstallation");
        options.subtitleFormat = locale.get("unpackingFormat");

        options.subtitleFormat = locale.get("downloadingFormat");
        options.subtitle = version.name;

        String fileUrl = version.download == null ?
                this.components.get(ManifestsURLs.class).getBaseDataURL()
                        + "/versions/" + StringUtils.urlEncode(version.name) + "/" + StringUtils.urlEncode(version.name) + "." + (version.singleJar ? "jar" : "zip")
                : version.download.getDownloadUrl();

        URLCollector urlCollector;

        try {
            urlCollector = new URLCollector(fileUrl);
        } catch(URISyntaxException e) {
            LOG.severe("failed to install version: ");
            LOG.logp(Level.SEVERE, "io.github.spookylauncher.VersionsInstaller", "install", "Throw!", e);
            return;
        }

        Downloader downloader = components.get(Downloader.class);

        Downloader.IDownloadMethod method = version.singleJar ? downloader::download : downloader::downloadAndUnpackZip;
        method.download(
                version.singleJar ? new File(versionDir, "minecraft.jar") : versionDir,
                urlCollector,
                options,
                success -> {
                    uiProvider.panel().setEnabledButtons(true);

                    if(success) {
                        LOG.info("version successfully installed");
                        uiProvider.panel().getButton(TitlePanel.PLAY).setText(locale.get("play"));
                    }
                }
        );
    }
}