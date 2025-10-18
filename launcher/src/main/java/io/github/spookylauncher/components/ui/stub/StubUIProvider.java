package io.github.spookylauncher.components.ui.stub;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.ui.spi.*;

import java.io.IOException;

public final class StubUIProvider extends LauncherComponent implements UIProvider {

    private Dialogs dialogs;
    private TitlePanel panel;
    private MainWindow window;
    private Messages messages;

    public StubUIProvider(
            final ComponentsController components
    ) {
        super("Stub UI Provider", components);
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        window = new StubMainWindowImpl(components);
        panel = new StubPanelImpl(components);
        dialogs = new StubDialogsImpl(components);
        messages = new StubMessagesImpl(components);

        window.setVisible(true);
    }

    @Override
    public Dialogs dialogs() {
        return dialogs;
    }

    @Override
    public TitlePanel panel() {
        return panel;
    }

    @Override
    public MainWindow window() {
        return window;
    }

    @Override
    public Messages messages() {
        return messages;
    }
}