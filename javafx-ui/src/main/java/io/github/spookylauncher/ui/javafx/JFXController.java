package io.github.spookylauncher.ui.javafx;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class JFXController {

    public Button articleButton, playButton, versionsButton, settingsButton;
    public Parent root;

    public void initialize() {
        applySettings(root);
    }

    private void applySettings(Parent root) {
        for (Node node : root.getChildrenUnmodifiable()) {
            node.setPickOnBounds(false);

            if (node instanceof Parent) {
                applySettings((Parent) node);
            }
        }
    }
}
