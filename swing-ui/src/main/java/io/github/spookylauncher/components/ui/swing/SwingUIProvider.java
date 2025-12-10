package io.github.spookylauncher.components.ui.swing;

import javax.swing.plaf.synth.*;

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

        SynthLookAndFeel synth = new SynthLookAndFeel();

        try {
            synth.load(Resource.getInput("style.xml"), getClass());
        } catch (Exception e) {
            throw new IOException(e);
        }

        try {
            UIManager.setLookAndFeel(synth);
        } catch (UnsupportedLookAndFeelException e) {
            LOG.warning("failed to initialize Synth L&F");
            LOG.logp(Level.WARNING, "io.github.spookylauncher.components.ui.swing.SwingUIProvider", "initialize", "THROW", e);
            LOG.warning("initializing system L&F instead");

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                LOG.severe("failed to initialize swing ui");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ui.swing.SwingUIProvider", "initialize", "THROW", ex);
                return;
            }
        }

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