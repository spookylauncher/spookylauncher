package io.github.spookylauncher.components.ui.javafx;

import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.ui.*;

public class JavaFxUiProvider extends LauncherComponent implements UIProvider {
    public JavaFxUiProvider(ComponentsController components) {
        super("JavaFX UI Provider", components);
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