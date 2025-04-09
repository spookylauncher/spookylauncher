package io.github.spookylauncher.components;

import io.github.spookylauncher.components.events.Events;
import io.github.spookylauncher.components.launch.GameLauncher;
import io.github.spookylauncher.components.ui.stub.StubUIProvider;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.components.ui.spi.UIProvider;
import io.github.spookylauncher.tree.jre.JREsManifest;
import io.github.spookylauncher.tree.launcher.LauncherManifest;
import io.github.spookylauncher.tree.mirrors.MirrorsManifest;
import io.github.spookylauncher.tree.versions.LibrariesManifest;

import java.io.File;
import java.util.*;

public final class ComponentsRegister {
    private final ComponentsController controller;
    private final File workDirectory;
    private final List<Integer> priority = new ArrayList<>(), async = new ArrayList<>();

    private final File launcherDirectory;

    public ComponentsRegister(ComponentsController controller, File workDirectory) {
        this.controller = controller;
        this.workDirectory = workDirectory;
        this.launcherDirectory = new File(workDirectory, "launcher");
    }

    private void createComponents(List<Integer> priority, List<Integer> async) {
        String launcherManifestDownloaderName = "launcher_manifest";
        String librariesManifestDownloaderName = "libraries_manifest";
        String JREsManifestDownloaderName = "jres_manifest";
        String mirrorsManifestDownloaderName = "mirrors_manifest";

        ManifestsURLs manifestsURLs = new ManifestsURLs(controller);

        int manifestsURLsId = controller.put(manifestsURLs);
        int jreController = controller.put(new JREController(controller, new File(launcherDirectory, "java"), JREsManifestDownloaderName));
        int libsController = controller.put(new LibrariesController(controller, new File(workDirectory, "libraries"), librariesManifestDownloaderName));
        int logsController = controller.put(new LogsController(controller, launcherDirectory));
        int optionsController = controller.put(new OptionsController(controller, new File(launcherDirectory, "options.json")));
        int versionsList = controller.put(new VersionsList(controller, new File(this.workDirectory, "versions"), manifestsURLs.getVersionsManifestURL()));
        int versionsInstaller = controller.put(new VersionsInstaller(controller, librariesManifestDownloaderName, mirrorsManifestDownloaderName));
        int discordPresenceViewer = controller.put(new DiscordPresenceViewer(controller));
        int gameLauncher = controller.put(new GameLauncher(controller, librariesManifestDownloaderName, this.workDirectory));
        int translator = controller.put(new Translator(controller));
        int uiProvider = controller.put(UIProvider.class, new SwingUIProvider(controller, JREsManifestDownloaderName));
        int downloader = controller.put(new Downloader(controller));
        int errorHandler = controller.put(new ErrorHandler(controller));
        int protocolHandler = controller.put(new ProtocolHandler(controller));
        int events = controller.put(new Events(controller));

        int launcherManifestDownloader = controller.put(launcherManifestDownloaderName, new ManifestDownloader<>(controller, LauncherManifest.class, manifestsURLs.getLauncherManifestURL()));
        int librariesManifestDownloader = controller.put(librariesManifestDownloaderName, new ManifestDownloader<>(controller, LibrariesManifest.class, manifestsURLs.getLibrariesManifestURL()));
        int JREsManifestDownloader = controller.put(JREsManifestDownloaderName, new ManifestDownloader<>(controller, JREsManifest.class, manifestsURLs.getJREsManifestURL()));
        int mirrorsManifestDownloader = controller.put(mirrorsManifestDownloaderName, new ManifestDownloader<>(controller, MirrorsManifest.class, manifestsURLs.getMirrorsManifestURL()));

        async.add(launcherManifestDownloader);
        async.add(librariesManifestDownloader);
        async.add(versionsList);

        priority.add(logsController);
        priority.add(events);
        priority.add(optionsController);
        priority.add(translator);
        priority.add(errorHandler);
        priority.add(launcherManifestDownloader);
        priority.add(librariesManifestDownloader);
        priority.add(versionsList);
        priority.add(jreController);
        priority.add(uiProvider);
        priority.add(discordPresenceViewer);

        Events eventsObj = controller.get(events);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> eventsObj.emitAndUnsubscribeAll(Events.SHUTDOWN)));
    }

    public void createComponents() {
        createComponents(priority, async);
    }

    public void initializeComponents() {
        controller.initializeComponents(priority, async);
    }
}