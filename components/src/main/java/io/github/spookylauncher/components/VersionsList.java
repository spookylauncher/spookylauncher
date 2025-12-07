package io.github.spookylauncher.components;

import io.github.spookylauncher.components.ui.TitlePanel;
import io.github.spookylauncher.components.ui.UIProvider;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.tree.versions.VersionsManifest;
import java.io.File;

public final class VersionsList extends ManifestDownloader<VersionsManifest> {
    public final File versionsDirectory;

    public VersionsList(
            final ComponentsController components,
            final File versionsDirectory,
            final String versionsManifestURL
    ) {
        super(components, VersionsManifest.class, versionsManifestURL);
        this.setName("Versions List");
        this.versionsDirectory = versionsDirectory;
    }

    public String[] getInstalledVersions() {
        return this.versionsDirectory.list();
    }

    public boolean isInstalled(VersionInfo version) {
        if(!this.versionsDirectory.exists()) return false;

        for(String name : getInstalledVersions()) if(name.equals(version.name)) return true;

        return false;
    }

    public VersionInfo getSelectedVersionInfo() {
        final String repo = this.components.get(ManifestsURLs.class).getBaseDataURL();

        return manifest.getVersionInfo(repo, components.get(OptionsController.class).getOptions().selectedVersion);
    }

    @Override
    public boolean downloadManifest() {
        if(super.downloadManifest()) {
            final UIProvider uiProvider = components.get(UIProvider.class);

            final Runnable task = () -> {
                if (
                        uiProvider.panel().getCurrentVersion() == null && components.get(OptionsController.class).getOptions().selectedVersion != null
                ) uiProvider.panel().setVersion(this.getSelectedVersionInfo());

                uiProvider.panel().setEnabledButton(TitlePanel.VERSIONS, true);
            };

            if(uiProvider.panel() == null) {
                components.addOnInitializedEvent(
                        components.getIndex((LauncherComponent) uiProvider),
                        c -> {
                            task.run();
                            return false;
                        }
                );
            } else
                task.run();

            return true;
        } else return false;
    }
}