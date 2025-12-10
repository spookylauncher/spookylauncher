package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.components.ui.swing.forms.VersionsDialog;
import io.github.spookylauncher.tree.launcher.Options;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

final class OpenVersionsEvent extends Event {

    private static final Logger LOG = Logger.getLogger("open versions event");

    OpenVersionsEvent(final ComponentsController components, final SwingUIProvider provider) {
        super(components, provider);
    }

    @Override
    public void run() {
        OptionsController optionsController = components.get(OptionsController.class);
        VersionsList versions = components.get(VersionsList.class);

        Options options = optionsController.getOptions();

        VersionsDialog dialog = new VersionsDialog(
                options.selectedVersion,
                versions.getManifest().getVersionsList(),
                (String version) -> {
                    if (Objects.equals(options.selectedVersion, version)) return;

                    options.selectedVersion = version;

                    provider.panel().setVersion(versions.getSelectedVersionInfo());

                    try {
                        optionsController.store();
                    } catch (IOException e) {
                        LOG.severe("failed to store options");
                        LOG.throwing("io.github.spookylauncher.components.ui.swing.events.OpenVersionsEvent", "run", e);
                    }
                });

        dialog.setResizable(false);

        dialog.setSize(250, 303);

        dialog.setLocationRelativeTo(provider.getFrame());

        dialog.buttonCancel.setText(components.get(Translator.class).get("cancel"));

        dialog.setVisible(true);
    }
}