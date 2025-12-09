package io.github.spookylauncher.components;

import io.github.spookylauncher.components.ui.UIProvider;
import io.github.spookylauncher.components.ui.Messages;
import io.github.spookylauncher.tree.jre.ExternalJreInfo;
import io.github.spookylauncher.tree.jre.JREsManifest;
import io.github.spookylauncher.tree.launcher.Options;
import io.github.spookylauncher.tree.jre.JreInfo;
import io.github.spookylauncher.tree.jre.SelectedJavaType;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.io.OSType;
import io.github.spookylauncher.io.IOUtils;
import io.github.spookylauncher.io.collectors.FileCollector;
import io.github.spookylauncher.io.collectors.URLCollector;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class JREController extends LauncherComponent {
    private static final Logger LOG = Logger.getLogger("jre controller");

    private final File javaDir;
    private JreInfo[] foundJREs;

    private final String manifestDownloaderName;

    public JREController(ComponentsController components, File javaDir, String manifestDownloaderName) {
        super("JRE Controller", components);
        this.javaDir = new File(javaDir, OSType.CURRENT.name.toLowerCase());
        this.manifestDownloaderName = manifestDownloaderName;
    }

    private JREsManifest getManifest() {
        return ( (ManifestDownloader<JREsManifest>) components.get(manifestDownloaderName)).getManifest();
    }

    private void safeOptionsStore() {
        try {
            components.get(OptionsController.class).store();
        } catch (IOException e) {
            LOG.severe("failed to store options");
            LOG.throwing("io.github.spookylauncher.components.JREController", "safeOptionsStore", e);
        }
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        this.checkJre();
    }
    
    public void checkJre() {
        final Translator localeController = components.get(Translator.class);
        final OptionsController optionsController = components.get(OptionsController.class);
        final Locale locale = localeController.getLocale();
        final Options options = optionsController.getOptions();
        final Messages messages = components.get(UIProvider.class).messages();

        if(options.selectedJavaType == null) {
            JreInfo[] jres = findJres();

            if(jres.length == 0) {
                LOG.warning("no available JREs");

                messages.error(locale.get("error"), locale.get("noJre"));
            } else {
                selectJre(jres[JreInfo.getIndexOfNewer(jres)]);

                safeOptionsStore();
            }
        }
    }

    public JreInfo[] findJres() {
        if(foundJREs == null) {
            List<JreInfo> jres = new ArrayList<>();

            File[] dirs = javaDir.listFiles();

            if(dirs != null) {
                for(File dir : dirs) jres.add(getManifest().findJreInfo(dir.getName()));
            }

            File javaDir;

            String[] jresPaths;

            try {
                jresPaths = IOUtils.locate("java");
            } catch(IOException | InterruptedException e) {
                LOG.severe("failed to locate jre's: ");
                LOG.throwing("io.github.spookylauncher.components.JREController", "findJres", e);
                return null;
            }

            for(String javaPath : jresPaths) {
                javaDir = new File(javaPath).getParentFile().getParentFile();

                ExternalJreInfo jre = new ExternalJreInfo(javaPath);

                Properties props;

                try {
                    props = new FileCollector(new File(javaDir, "release")).collectProperties();
                } catch (IOException e) {
                    LOG.severe("failed to read info of jre \"" + javaPath + "\"");
                    LOG.throwing("io.github.spookylauncher.components.JREController", "findJres", e);
                    continue;
                }

                jre.vendor = props.getProperty("IMPLEMENTOR");
                jre.vendor = jre.vendor.substring(1, jre.vendor.length() - 1);

                jre.fullVersion = props.getProperty("JAVA_VERSION");
                jre.fullVersion = jre.fullVersion.substring(1, jre.fullVersion.length() - 1);

                try {
                    String[] splits = jre.fullVersion.split("\\.");

                    jre.majorVersion = Integer.parseInt(splits[0].equals("1") ? splits[1] : splits[0]);
                } catch(Exception e) {
                    LOG.throwing("io.github.spookylauncher.components.JREController", "findJres", e);
                    jre.majorVersion = -1;
                }

                jres.add(jre);
            }

            foundJREs = jres.toArray(new JreInfo[0]);
        }

        return foundJREs;
    }

    public boolean isSelected(JreInfo info) {
        return getSelectedJre().equals(info);
    }

    public File getJreDirectory(JreInfo info) {
        if(info instanceof ExternalJreInfo) return new File(((ExternalJreInfo) info).path).getParentFile().getParentFile();
        else return new File(javaDir, info.fullVersion);
    }

    public boolean uninstallJre(JreInfo info) {
        if(!isInstalled(info)) return false;

        try {
            IOUtils.deleteTree(getJreDirectory(info));
        } catch (IOException e) {
            LOG.severe("failed to delete tree \"" + getJreDirectory(info).getAbsolutePath() + "\"");
            LOG.throwing("io.github.spookylauncher.components.JREController", "uninstallJre", e);
        }

        return true;
    }
    public boolean isInstalled(JreInfo info) {
        return getJreDirectory(info).exists();
    }

    public boolean selectExternalJre(String executablePath) {
        OptionsController optionsController = components.get(OptionsController.class);
        Options options = optionsController.getOptions();

        options.selectedJavaType = SelectedJavaType.EXTERNAL;
        options.selectedJavaPath = new File(executablePath).getAbsolutePath();

        safeOptionsStore();

        return true;
    }

    public void selectCustomJre(String executablePath) {
        OptionsController optionsController = components.get(OptionsController.class);
        Options options = optionsController.getOptions();

        options.selectedJavaType = SelectedJavaType.CUSTOM;
        options.customJavaPath = new File(executablePath).getAbsolutePath();

        safeOptionsStore();
    }

    public boolean selectJre(JreInfo info) {
        if(info instanceof ExternalJreInfo) return selectExternalJre(((ExternalJreInfo) info).path);

        if(!isInstalled(info)) return false;

        OptionsController optionsController = components.get(OptionsController.class);
        Options options = optionsController.getOptions();

        options.selectedJava = info.fullVersion;
        options.selectedJavaType = SelectedJavaType.LAUNCHER;
        options.selectedJavaPath = getJreExecutable(info).getAbsolutePath();

        safeOptionsStore();

        return true;
    }
    public JreInfo getSelectedJre() {
        Options options = components.get(OptionsController.class).getOptions();

        switch(options.selectedJavaType) {
            case CUSTOM: return new ExternalJreInfo(options.customJavaPath);
            case EXTERNAL: return new ExternalJreInfo(options.selectedJavaPath);
            default: return getManifest().findJreInfo(options.selectedJava);
        }
    }
    public File getJreExecutable(JreInfo info) {
        if(info instanceof ExternalJreInfo) return new File(((ExternalJreInfo) info).path);
        if(!isInstalled(info)) return null;

        return IOUtils.find("bin/" + (OSType.CURRENT == OSType.WINDOWS ? "java.exe" : "java"), getJreDirectory(info));
    }

    public void installJre(JreInfo info, Consumer<Boolean> onInstalled) {
        LOG.info("start installing JRE " + info.fullVersion + ". Vendor: " + info.vendor + ", Major version: " + info.majorVersion);

        final Locale locale = components.get(Translator.class).getLocale();
        final UIProvider uiProvider = components.get(UIProvider.class);

        String url;

        if(info.downloads.containsKey(OSType.CURRENT)) url = info.downloads.get(OSType.CURRENT);
        else {
            LOG.severe("JRE installation failed because downloads are not available");
            uiProvider.messages().error
            (
                    locale.get("installationError"),
                    String.format(locale.get("jreDownloadNotExists"), info.fullVersion)
            );
            return;
        }

        new Thread(
                () -> {
                    assert OSType.CURRENT != null;

                    File destination = getJreDirectory(info);

                    if(!isInstalled(info)) {
                        destination.mkdirs();

                        uiProvider.panel().setEnabledButtons(false);

                        Downloader.Options options = new Downloader.Options();

                        options.title = String.format(locale.get("jreInstallation"), info.fullVersion, info.vendor);
                        options.subtitleFormat = locale.get("unpackingFormat");
                        options.consumeFullPaths = false;

                        URLCollector urlCollector;

                        try {
                            urlCollector = new URLCollector(url);
                        } catch(URISyntaxException e) {
                            LOG.severe("failed to install jre: ");
                            LOG.throwing("io.github.spookylauncher.components.JREController", "installJre", e);
                            return;
                        }

                        components.get(Downloader.class).downloadAndUnpackZip(
                                destination,
                                urlCollector,
                                options,
                                success -> {
                                    uiProvider.panel().setEnabledButtons(true);

                                    if(!success) {
                                        LOG.info("JRE installation canceled by user");
                                        uiProvider.messages().info(
                                                locale.get("jreInstallationFailed"),
                                                locale.get("operationCanceledByUser")
                                        );
                                    } else LOG.info("JRE successfully installed");

                                    onInstalled.accept(success);
                                }
                        );
                    }
                }
        ).start();
    }
}