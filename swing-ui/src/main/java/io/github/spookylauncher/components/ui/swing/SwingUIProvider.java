package io.github.spookylauncher.components.ui.swing;

import javax.swing.plaf.synth.*;

import com.formdev.flatlaf.FlatDarkLaf;
import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.ui.*;
import io.github.spookylauncher.components.ui.swing.events.EventsRegister;
import io.github.spookylauncher.io.Resource;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SwingUIProvider extends LauncherComponent implements UIProvider {

    private static final Logger LOG = Logger.getLogger("swing ui");
    private JFrame frame;

    private final String jresManifestDownloaderName;

    private Dialogs dialogs;
    private TitlePanel panel;
    private MainWindow window;
    private Messages messages;

    public JFrame getFrame() {
        return this.frame;
    }

    void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public SwingUIProvider(
            final ComponentsController components,
            final String jresManifestDownloaderName
    ) {
        super("Swing UI Provider", components);

        this.jresManifestDownloaderName = jresManifestDownloaderName;
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        AssetsLoader.load();

        FlatDarkLaf.setup();

        window = new MainWindowImpl(components, this);
        panel = new TitlePanelImpl(components, frame);
        dialogs = new DialogsImpl(components, this);
        messages = new MessagesImpl(components, this);

        EventsRegister.register(components, this, jresManifestDownloaderName);

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