package io.github.spookylauncher.ui.javafx;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class JFXController {
    public VBox contentPanel;
    public Label statusLabel;

    public void onHomeClicked(ActionEvent actionEvent) {
        System.out.println("home");
    }

    public void onSettingsClicked(ActionEvent actionEvent) {
        System.out.println("settings");
    }

    public void onAboutClicked(ActionEvent actionEvent) {
        System.out.println("about");
    }
}
