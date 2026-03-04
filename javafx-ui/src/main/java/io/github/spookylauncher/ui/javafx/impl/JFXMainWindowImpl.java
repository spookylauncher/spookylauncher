package io.github.spookylauncher.ui.javafx.impl;

import io.github.spookylauncher.ui.MainWindow;
import io.github.spookylauncher.ui.javafx.Util;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;

public class JFXMainWindowImpl implements MainWindow {
    private final Stage stage;

    public JFXMainWindowImpl(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void toTopFront() {
        Platform.runLater(stage::toFront);
    }

    @Override
    public Image getIcon() {
        return Util.jfxImageToAwt(
            Util.call(() -> stage.getIcons().get(0))
        );
    }

    @Override
    public boolean isVisible() {
        return Util.call(stage::isShowing);
    }

    @Override
    public void setVisible(boolean visible) {
        Platform.runLater(() -> {
            if(visible)
                stage.show();
            else
                stage.hide();
        });
    }
}
