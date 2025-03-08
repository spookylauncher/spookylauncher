package io.github.spookylauncher.components.ui.spi;

import java.awt.*;

public interface MainWindow {
    void toTopFront();

    Image getIcon();

    boolean isVisible();

    void setVisible(boolean visible);
}