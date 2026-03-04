package io.github.spookylauncher.ui.javafx;

import io.github.spookylauncher.components.*;
import io.github.spookylauncher.ui.*;
import javafx.application.Application;

import java.io.IOException;

public class JFXUIProvider extends LauncherComponent implements UIProvider {
    public JFXUIProvider(ComponentsController components, String jresManifestDownloaderName) {
        super("JavaFX UI Provider", components);
    }

    @Override
    public void initialize() throws IOException {
        Application.launch(JFXApplication.class);
    }

    @Override
    public Dialogs dialogs() {
        return null;
    }

    @Override
    public TitlePanel panel() {
        return null;
    }

    @Override
    public MainWindow window() {
        return null;
    }

    @Override
    public Messages messages() {
        return null;
    }
}
