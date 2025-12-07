package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.ui.MessageType;
import io.github.spookylauncher.components.ui.Messages;

import javax.swing.*;
import java.util.*;

class MessagesImpl extends LauncherComponent implements Messages {

    private static final Map<MessageType, Integer> spiTypeToPaneType = new HashMap<>();

    private final SwingUIProvider provider;

    MessagesImpl(final ComponentsController components, final SwingUIProvider provider) {
        super("Swing API Messages", components);
        this.provider = provider;
    }

    @Override
    public void message(String title, String message, MessageType type) {
        JOptionPane.showMessageDialog(provider.getFrame(), message, title, spiTypeToPaneType.get(type));
    }

    static {
        spiTypeToPaneType.put(MessageType.INFO, JOptionPane.INFORMATION_MESSAGE);
        spiTypeToPaneType.put(MessageType.WARNING, JOptionPane.WARNING_MESSAGE);
        spiTypeToPaneType.put(MessageType.ERROR, JOptionPane.ERROR_MESSAGE);
    }
}