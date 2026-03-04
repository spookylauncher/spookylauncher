package io.github.spookylauncher.ui.stub;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.ui.MessageType;
import io.github.spookylauncher.ui.Messages;

class StubMessagesImpl extends LauncherComponent implements Messages {

    StubMessagesImpl(final ComponentsController components) {
        super("Stub Messages", components);
    }

    @Override
    public void message(String title, String message, MessageType type) {}
}
