package io.github.spookylauncher.components.ui.stub;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.ui.MainWindow;
import java.awt.*;

class StubMainWindowImpl extends LauncherComponent implements MainWindow {

    StubMainWindowImpl(final ComponentsController components) {
        super("Stub Main Window", components);
    }

    @Override
    public void toTopFront() { }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) { }
}