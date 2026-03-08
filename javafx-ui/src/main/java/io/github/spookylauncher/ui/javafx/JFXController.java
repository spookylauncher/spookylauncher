package io.github.spookylauncher.ui.javafx;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class JFXController {

    public Button articleButton, playButton, versionsButton, settingsButton;
    public ImageView versionPreview;
    public Pane versionPreviewPane;
    public Parent root;

    public void initialize() {
        applySettings(root);

        versionPreview.fitWidthProperty().bind(versionPreviewPane.widthProperty());
        versionPreview.fitHeightProperty().bind(versionPreviewPane.heightProperty());

        versionPreview.setSmooth(true);
        versionPreview.setManaged(false);
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
