package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.components.ui.spi.TitlePanel;

public final class EventsRegister {
    public static void register(
            final ComponentsController components,
            final SwingUIProvider provider,
            final String jresManifestName
    ) {
        TitlePanel panel = provider.panel();

        panel.addActionEvent(TitlePanel.OPEN_ARTICLE, new OpenArticleEvent(components, provider));
        panel.addActionEvent(TitlePanel.SETTINGS, new OpenSettingsEvent(components, provider));
        panel.addActionEvent(TitlePanel.VERSIONS, new OpenVersionsEvent(components, provider));
        panel.addActionEvent(TitlePanel.PLAY, new PlayEvent(components, provider, jresManifestName));
    }
}