package io.github.spookylauncher.ui;

public interface UIProvider {
    Dialogs dialogs();

    TitlePanel panel();

    MainWindow window();

    Messages messages();

    void shutdown();
}
