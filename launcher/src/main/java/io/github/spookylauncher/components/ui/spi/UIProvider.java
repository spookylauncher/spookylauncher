package io.github.spookylauncher.components.ui.spi;

public interface UIProvider {
    Dialogs dialogs();

    TitlePanel panel();

    MainWindow window();

    Messages messages();
}