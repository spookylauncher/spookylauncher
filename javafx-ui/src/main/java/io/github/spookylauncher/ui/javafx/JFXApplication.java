package io.github.spookylauncher.ui.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class JFXApplication extends Application {
    private Scene scene;
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JFXProxy.setApp(this);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/assets/icon.png")
        )));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 434);

        primaryStage.setTitle("Spooky Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();

        stage = primaryStage;

        JFXProxy.getInitLatch().countDown();
    }
}
