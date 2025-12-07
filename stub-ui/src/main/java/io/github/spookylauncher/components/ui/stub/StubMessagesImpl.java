package io.github.spookylauncher.components.ui.stub;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.ui.MessageType;
import io.github.spookylauncher.components.ui.Messages;

class StubMessagesImpl extends LauncherComponent implements Messages {

    StubMessagesImpl(final ComponentsController components) {
        super("Stub Messages", components);
    }

    @Override
    public void message(String title, String message, MessageType type) { }
}