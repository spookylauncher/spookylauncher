package io.github.spookylauncher.ui;

import java.awt.*;

public interface MainWindow {
    void toTopFront();

    Image getIcon();

    boolean isVisible();

    void setVisible(boolean visible);
}
