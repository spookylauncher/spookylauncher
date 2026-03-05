package io.github.spookylauncher.ui.javafx.impl;

import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.ui.Button;
import io.github.spookylauncher.ui.Panel;
import io.github.spookylauncher.ui.TitlePanel;
import io.github.spookylauncher.util.Locale;

import java.awt.*;

public class JFXTitlePanel implements TitlePanel {
    @Override
    public void setPreview(Image image) {

    }

    @Override
    public void setVersion(VersionInfo info) {

    }

    @Override
    public VersionInfo getCurrentVersion() {
        return null;
    }

    @Override
    public Panel getChild(String name) {
        return null;
    }

    @Override
    public void setEnabledButtons(boolean enabled, String... buttons) {

    }

    @Override
    public void setLocale(Locale locale) {

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
    public void addActionEvent(String button, Runnable event) {

    }

    @Override
    public boolean isButtonEnabled(String button) {
        return false;
    }

    @Override
    public void setEnabledButton(String button, boolean enabled) {

    }
}
