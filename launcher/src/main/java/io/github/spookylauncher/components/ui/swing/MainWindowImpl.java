package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.advio.ResourceCollector;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.events.Events;
import io.github.spookylauncher.components.ui.spi.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MainWindowImpl extends LauncherComponent implements MainWindow {

    private final JFrame frame;

    private final Image icon;

    MainWindowImpl(final ComponentsController components, final SwingUIProvider provider) {
        super("Swing API Main Window", components);

        this.icon = new ResourceCollector("icon.png").collectImage();

        frame = new JFrame("Spooky Launcher");
        frame.setSize(800, 434);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                components.get(Events.class).emitAndUnsubscribeAll(Events.SHUTDOWN);

                System.exit(0);
            }
        });

        frame.setIconImage(icon);

        frame.setLocationRelativeTo(null);

        provider.setFrame(frame);
    }

    @Override
    public void toTopFront() {
        EventQueue.invokeLater(() -> {
            int state = frame.getExtendedState();
            state &= ~JFrame.ICONIFIED;
            frame.setExtendedState(state);
            frame.setAlwaysOnTop(true);
            frame.toFront();
            frame.requestFocus();
            frame.setAlwaysOnTop(false);
            frame.repaint();
        });
    }

    @Override
    public Image getIcon() {
        return icon;
    }

    @Override
    public boolean isVisible() {
        return frame.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}