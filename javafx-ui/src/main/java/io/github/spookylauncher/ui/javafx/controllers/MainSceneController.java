package io.github.spookylauncher.ui.javafx.controllers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class MainSceneController {

    public Button articleButton, playButton, versionsButton, settingsButton;
    public Label versionNameLabel, developerLabel, releaseDateLabel;
    public Pane versionPreviewPane;
    public Parent root;

    public void initialize() {
        applySettings(root);

        Image image = new Image(getClass().getResourceAsStream("/assets/img.png"));

        BackgroundImage bgImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                1.0, 1.0,
                true, true,
                false, false
            )
        );

        versionPreviewPane.setBackground(new Background(bgImage));

        int backgroundRadius = 25;

        Rectangle clip = new Rectangle();

        clip.setArcWidth(backgroundRadius * 2);
        clip.setArcHeight(backgroundRadius * 2);
        clip.widthProperty().bind(versionPreviewPane.widthProperty());
        clip.heightProperty().bind(versionPreviewPane.heightProperty());

        versionPreviewPane.setClip(clip);
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
