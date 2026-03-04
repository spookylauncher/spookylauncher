package io.github.spookylauncher.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class JFXApplication extends Application {
    private Scene scene;
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JFXProxy.setApp(this);

        Label label = new Label("Hello, JavaFX!");
        Scene scene = new Scene(label, 800, 434);

        primaryStage.setTitle("Spooky Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();

        stage = primaryStage;
    }
}
