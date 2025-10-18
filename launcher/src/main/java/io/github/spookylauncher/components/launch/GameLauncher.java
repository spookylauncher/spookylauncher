package io.github.spookylauncher.components.launch;

import io.github.spookylauncher.Constants;
import io.github.spookylauncher.advio.IOUtils;
import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.debug.LocalLibraries;
import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.GameStartData;
import io.github.spookylauncher.components.LogsController;
import io.github.spookylauncher.components.ui.spi.UIProvider;
import io.github.spookylauncher.components.ui.spi.TitlePanel;
import io.github.spookylauncher.tree.LibrariesCollection;
import io.github.spookylauncher.tree.LibraryInfo;
import io.github.spookylauncher.tree.jre.JreInfo;
import io.github.spookylauncher.tree.versions.LibrariesManifest;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.tree.launcher.Options;
import io.github.spookylauncher.util.CommandTokenizer;
import io.github.spookylauncher.util.Locale;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameLauncher extends LauncherComponent {
    private final String manifestDownloaderName;
    private final File workDirectory;
    private final File logsDirectory;
    private final VMArgumentsSelector vmArgumentsSelector = new VMArgumentsSelector();

    public GameLauncher(ComponentsController components, String manifestDownloaderName, File workDirectory) {
        super("Game Launcher", components);
        this.manifestDownloaderName = manifestDownloaderName;
        this.workDirectory = workDirectory;
        this.logsDirectory = new File(this.workDirectory, "logs");

        if(!this.logsDirectory.exists() && !this.logsDirectory.mkdirs()) log(Level.ERROR, "failed to create game logs directory!");
    }
    public void launch(final VersionInfo version) {
        final Locale locale = components.get(Translator.class).getLocale();
        final UIProvider uiProvider = components.get(UIProvider.class);
        final Options options = components.get(OptionsController.class).getOptions();
        final LogsController logs = components.get(LogsController.class);
        final VersionsInstaller versionsInstaller = components.get(VersionsInstaller.class);

        if(options.selectedJavaType == null) {
            log(Level.ERROR, "no selected java");
            uiProvider.messages().error(locale.get("error"), locale.get("noJre"));
            return;
        }

        final File log = logs.createNewLog(logsDirectory);

        if(log == null) return;

        uiProvider.panel().setEnabledButtons(false, TitlePanel.VERSIONS, TitlePanel.PLAY);

        final Runnable launchTask = () -> {
            boolean success =
                    startProcess
                            (
                                    version,
                                    new File
                                            (
                                                    components.get(VersionsList.class).versionsDirectory,
                                                    version.name
                                            ),
                                    logs,
                                    log,
                                    new File(logsDirectory, "latest.log")
                            );

            if(success) {
                uiProvider.window().setVisible(false);

                if(options.discordPresence) components.get(DiscordPresenceViewer.class).showGamePresence(version);
            }
        };

        log(Level.INFO, "installing libraries for version " + version.name);

        versionsInstaller.installLibraries(
                version,
                success -> {
                    if(success) {
                        log(Level.INFO, "libraries successfully installed");
                        new Thread(launchTask).start();
                    } else
                        log(Level.ERROR, "failed to install libraries");

                }
        );
    }
    private List<LibraryInfo> unpackLibraries(LibrariesManifest manifest, LibrariesCollection collection) {
        List<LibraryInfo> list = new ArrayList<>();

        for(LibraryInfo libInfo : collection.libraries) list.add(manifest.getLibrary(libInfo.name, libInfo.version));

        for(String includedCollectionName : collection.collections) {
            LibrariesCollection includedCollection = manifest.getLibsCollection(includedCollectionName);

            list.addAll(unpackLibraries(manifest, includedCollection));
        }

        return list;
    }

    private boolean startProcess(final VersionInfo version, final File dir, final LogsController logs, final File log, final File latestLog) {
        final JREController jreController = components.get(JREController.class);
        final Options options = components.get(OptionsController.class).getOptions();

        final List<String> cmd = new ArrayList<>();

        final StringBuilder classPathBuilder = new StringBuilder();
        final StringBuilder libraryPathBuilder = new StringBuilder();

        final LibrariesController libsController = components.get(LibrariesController.class);
        final LibrariesManifest manifest = ( (ManifestDownloader<LibrariesManifest>) components.get(manifestDownloaderName)).getManifest();

        final List<LibraryInfo> libraries = new ArrayList<>();

        final List<String> specialLibraryPath = new ArrayList<>();

        if(version.libsCollections != null) {
            for(String libs : version.libsCollections) {
                LibrariesCollection libsCollection = manifest.getLibsCollection(libs);

                libraries.addAll(unpackLibraries(manifest, libsCollection));
            }
        }

        if(version.libraries != null) {
            for (LibraryInfo library : version.libraries)
                libraries.add(manifest.getLibrary(library.name, library.version));
        }

        libraries.addAll(LocalLibraries.LOCAL_LIBS);

        final String relativePathToLibrariesPrefix = "../../" + (IOUtils.getRelativePath(workDirectory, libsController.getLibrariesDirectory())) + "/";

        for(LibraryInfo library : libraries) {
            File libFile = libsController.getLibraryFile(library);

            final String path = libFile.getAbsolutePath();

            if(library.isNative) {
                libraryPathBuilder.append(relativePathToLibrariesPrefix).append(IOUtils.getRelativePath(libsController.getLibrariesDirectory(), libFile));
                libraryPathBuilder.append(';');
                libraryPathBuilder.append(path);
                libraryPathBuilder.append(';');
                specialLibraryPath.add("-D" + library.packet + ".librarypath=" + path);
            } else {
                classPathBuilder.append(path);
                classPathBuilder.append(';');
            }
        }

        if(libraryPathBuilder.length() > 0) libraryPathBuilder.setLength(libraryPathBuilder.length() - 1);

        classPathBuilder.append(version.launchData.getOrDefault("classPath", "minecraft.jar"));
        //classPathBuilder.append(';');
        //classPathBuilder.append("");

        final JreInfo selectedJre = jreController.getSelectedJre();

        final String jrePath = jreController.getJreExecutable(selectedJre).getAbsolutePath();

        cmd.add(jrePath);
        cmd.add("-Xmx" + options.memory + "M");

        String vmArgs = null;

        if(version.launchData.containsKey("vmArgs")) vmArgs = version.getLaunchProperty("vmArgs");
        else if(version.launchData.containsKey("specificVmArgs")) {
            String specificVmArgs =
            vmArgumentsSelector.getVMArgs(
                    version.getLaunchProperty("specificVmArgs"), selectedJre.majorVersion
            );

            if(specificVmArgs != null) vmArgs = specificVmArgs;

        }

        if(vmArgs != null) cmd.addAll(CommandTokenizer.tokenize(vmArgs));

        final String classPath = classPathBuilder.toString();
        final String libraryPath = libraryPathBuilder.toString();

        cmd.add("-Dconsole.encoding=UTF-8");
        cmd.add("-Dfile.encoding=UTF-8");
        cmd.add("-Dorg.lwjgl.util.Debug=true");

        if(libraryPath.length() != 0) {
            cmd.add("-Djava.library.path=" + libraryPath);

            cmd.addAll(specialLibraryPath);
        }

        if(classPath.length() != 0) {
            cmd.add("-cp");
            cmd.add(classPath);
        }

        cmd.add(Constants.WRAPPER_MAIN);

        //cmd.add(version.getLaunchProperty("main", "Start"));

        final Properties properties = new Properties();

        final String nickname = options.getNickname();

        properties.setProperty("nickname", nickname);
        properties.setProperty("main", version.getLaunchProperty("main", Constants.DEFAULT_MC_MAIN));
        properties.setProperty("launcher.dir", "\"" + workDirectory.getAbsolutePath() + "\"");
        properties.setProperty("launcher.resourcesDir", "\"" + workDirectory.getAbsolutePath() + "/resources\"");

        String gameArgs = Constants.DEFAULT_GAME_ARGS;

        if(version.hasProperty("gameArgs"))
            gameArgs += " " + version.getLaunchProperty("gameArgs");

         for(String name : properties.stringPropertyNames())
             gameArgs = gameArgs.replace("${" + name + "}", properties.getProperty(name));

        if(!gameArgs.isEmpty()) cmd.addAll(CommandTokenizer.tokenize(gameArgs));

        final GameStartData data = new GameStartData();

        final StringBuilder cmdString = new StringBuilder();

        for(String str : cmd) {
            cmdString.append('"');
            cmdString.append(str);
            cmdString.append("\" ");
        }

        cmdString.setLength(cmdString.length() - 1);

        data.command = cmdString.toString();
        data.version = version.name;
        data.nickname = nickname;
        data.javaPath = jrePath;
        data.javaVendor = selectedJre.vendor;
        data.javaVersion = selectedJre.fullVersion;
        data.javaMajorVersion = selectedJre.majorVersion;

        try {
            final ProcessBuilder builder = new ProcessBuilder()
                    .directory(dir)
                    .command(cmd.toArray(new String[0]))
                    .redirectErrorStream(true);

            data.process = builder.start();
            data.uptime = System.currentTimeMillis();

            log(Level.INFO, "launching game");

            logs.startLogging(data, log, latestLog, () -> {
                final UIProvider uiProvider = components.get(UIProvider.class);

                uiProvider.panel().setEnabledButtons(true, TitlePanel.VERSIONS, TitlePanel.PLAY);

                uiProvider.window().setVisible(true);
            });

            return true;
        } catch(Exception e) {
            log(Level.ERROR, "failed to launch game");
            components.get(ErrorHandler.class).handleException("startError", e);
            return false;
        }
    }
}