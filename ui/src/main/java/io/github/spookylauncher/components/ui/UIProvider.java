package io.github.spookylauncher.components.ui;

public interface UIProvider {
    Dialogs dialogs();

    TitlePanel panel();

    MainWindow window();

    Messages messages();
}