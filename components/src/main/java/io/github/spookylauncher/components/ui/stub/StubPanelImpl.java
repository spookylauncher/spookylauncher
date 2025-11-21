package io.github.spookylauncher.components.ui.stub;

import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.ui.spi.Button;
import io.github.spookylauncher.components.ui.spi.Panel;
import io.github.spookylauncher.components.ui.spi.TitlePanel;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.util.Locale;
import java.awt.*;

class StubPanelImpl extends LauncherComponent implements TitlePanel {


    StubPanelImpl(final ComponentsController components) {
        super("Stub Title Panel", components);
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Button getButton(String button) {
        return null;
    }

    @Override
    public void addActionEvent(String button, Runnable event) { }

    @Override
    public boolean isButtonEnabled(String button) {
        return false;
    }

    @Override
    public void setEnabledButtons(boolean enabled, String... buttons) { }

    @Override
    public void setLocale(Locale locale) { }

    @Override
    public void setEnabledButton(String button, boolean enabled) { }

    @Override
    public void setPreview(Image image) { }

    @Override
    public void setVersion(VersionInfo info) { }

    @Override
    public VersionInfo getCurrentVersion() {
        return null;
    }

    @Override
    public Panel getChild(String name) {
        return null;
    }
}