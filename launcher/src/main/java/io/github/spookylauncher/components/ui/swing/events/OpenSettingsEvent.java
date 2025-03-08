package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.advio.Environment;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.JREController;
import io.github.spookylauncher.components.OptionsController;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.components.ui.swing.forms.SettingsDialog;
import io.github.spookylauncher.util.Locale;

class OpenSettingsEvent extends Event {

    private static final int MAX_MEMORY = (int) (Environment.MAX_PHYSICAL_MEMORY / 2097152);

    OpenSettingsEvent(final ComponentsController components, final SwingUIProvider provider) {
        super(components, provider);
    }

    @Override
    public void run() {
        Translator translator = components.get(Translator.class);

        SettingsDialog dialog = new SettingsDialog(
                256,
                MAX_MEMORY,
                components.get(JREController.class),
                provider.messages(),
                components.get(OptionsController.class),
                translator
        );

        Locale locale = translator.getLocale();

        dialog.ramLabel.setText(locale.get("ram"));
        dialog.ramVolumeLabel.setText(locale.get(("megabyte")));
        dialog.buttonCancel.setText(locale.get("cancel"));
        dialog.nicknameLabel.setText(locale.get("nickname"));
        dialog.jreSettingsButton.setText(locale.get("openJreSettings"));

        dialog.setIconImage(provider.window().getIcon());
        dialog.setTitle(locale.get("settings"));
        dialog.setSize(480, 225);
        dialog.setLocationRelativeTo(provider.getFrame());
        dialog.setResizable(false);

        dialog.setVisible(true);
    }
}