package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.VersionsList;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.util.Browser;

class OpenArticleEvent extends Event {

    OpenArticleEvent(final ComponentsController components, final SwingUIProvider provider) {
        super(components, provider);
    }

    @Override
    public void run() {

        VersionInfo version = components.get(VersionsList.class).getSelectedVersionInfo();

        if(version == null) return;

        String wiki = version.articles.get(components.get(Translator.class).getLocale().getLanguage());

        if(wiki == null) wiki = version.articles.get("fallback");

        if(wiki != null) Browser.openURL(wiki);
    }
}