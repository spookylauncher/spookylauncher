package io.github.spookylauncher.ui.javafx.controllers;

import io.github.spookylauncher.ui.javafx.JFXProxy;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainSceneController {

    @FXML public Button articleButton, playButton, versionsButton, settingsButton;
    @FXML public Label versionNameLabel, developerLabel, releaseDateLabel;
    @FXML public Pane versionPreviewPane;
    @FXML public Parent root;

    public void initialize() {
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

    @FXML
    public void onSettingsClicked() throws IOException {
        Stage dialog = new Stage();

        dialog.initOwner(JFXProxy.getApp().getStage());
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.getIcons().addAll(JFXProxy.getApp().getStage().getIcons());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 250);

        dialog.setTitle("Spooky Launcher: Settings");
        dialog.setScene(scene);

        dialog.show();
    }
}
