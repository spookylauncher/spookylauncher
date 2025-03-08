package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.launch.GameLauncher;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.components.ui.spi.Dialogs;
import io.github.spookylauncher.components.ui.spi.Messages;
import io.github.spookylauncher.tree.jre.JREsManifest;
import io.github.spookylauncher.tree.jre.JreInfo;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.util.Locale;

final class PlayEvent extends Event {

    private final String manifestDownloaderName;

    PlayEvent(final ComponentsController components, final SwingUIProvider provider, final String jresManifestName) {
        super(components, provider);
        this.manifestDownloaderName = jresManifestName;
    }

    @Override
    public void run() {
        VersionsList versions = components.get(VersionsList.class);
        VersionsInstaller installer = components.get(VersionsInstaller.class);

        Dialogs dialogs = provider.dialogs();
        Messages messages = provider.messages();

        VersionInfo selectedVersion = versions.getSelectedVersionInfo();

        Locale locale = provider.panel().getLocale();

        GameLauncher launcher = components.get(GameLauncher.class);

        if(!versions.isInstalled(selectedVersion)) installer.install(selectedVersion);
        else {
            JREsManifest manifest = ( (ManifestDownloader<JREsManifest>) components.get(manifestDownloaderName) ).getManifest();

            JreInfo jre = manifest.findJreInfoByMajorVersion(selectedVersion.javaMajorVersion);

            if(jre != null) {
                JREController jreController = components.get(JREController.class);

                if(jreController.isInstalled(jre)) {
                    if(!jreController.isSelected(jre)) {
                        dialogs.showConfirmDialog
                                (
                                        () -> jreController.selectJre(jre),
                                        locale.get("poorJreCompatibility"),
                                        String.format(locale.get("anotherJreRecommended"), jre.majorVersion)
                                );
                    }

                    launcher.launch(selectedVersion);
                } else {
                    dialogs.showConfirmDialog(
                            () -> jreController.installJre(jre,
                                    success -> {
                                        if(success) {
                                            messages.info(
                                                    locale.get("successfullyInstalled"),
                                                    String.format(locale.get("jreSuccessfullyInstalled"), jre.majorVersion)
                                            );
                                        }
                                    }
                            ),
                            locale.get("poorJreCompatibility"),
                            String.format(locale.get("anotherJreInstallRecommended"), jre.majorVersion),
                            () -> launcher.launch(selectedVersion)
                    );
                }
            } else launcher.launch(selectedVersion);
        }
    }
}