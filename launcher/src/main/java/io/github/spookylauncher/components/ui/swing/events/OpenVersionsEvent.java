package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.OptionsController;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.VersionsList;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.components.ui.swing.forms.VersionsDialog;
import io.github.spookylauncher.tree.launcher.Options;

import java.util.Objects;

final class OpenVersionsEvent extends Event {

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

                    optionsController.store();
                });

        dialog.setResizable(false);

        dialog.setSize(225, 303);

        dialog.setLocationRelativeTo(provider.getFrame());

        dialog.buttonCancel.setText(components.get(Translator.class).get("cancel"));

        dialog.setVisible(true);
    }
}