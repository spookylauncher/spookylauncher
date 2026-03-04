package io.github.spookylauncher.ui.javafx;

import io.github.spookylauncher.components.*;
import io.github.spookylauncher.ui.*;
import io.github.spookylauncher.ui.javafx.impl.JFXMainWindowImpl;
import io.github.spookylauncher.ui.javafx.impl.JFXMessagesImpl;
import javafx.application.Application;
import javafx.application.Platform;

import java.io.IOException;

public class JFXUIProvider extends LauncherComponent implements UIProvider {
    private JFXMainWindowImpl window;
    private JFXMessagesImpl messages;

    public JFXUIProvider(ComponentsController components, String jresManifestDownloaderName) {
        super("JavaFX UI Provider", components);
    }

    @Override
    public void initialize() throws IOException {
        JFXProxy.setProvider(this);
        Application.launch(JFXApplication.class);

        window = new JFXMainWindowImpl(JFXProxy.getApp().getStage());
        messages = new JFXMessagesImpl();
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
        return window;
    }

    @Override
    public Messages messages() {
        return messages;
    }

    @Override
    public void shutdown() {
        Util.call(() -> {
            JFXProxy.getApp().getStage().close();
            Platform.exit();
        });
    }
}
