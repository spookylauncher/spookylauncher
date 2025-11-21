package io.github.spookylauncher.components.ui.swing.events;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.ui.swing.SwingUIProvider;

abstract class Event implements Runnable {
    protected final ComponentsController components;
    protected final SwingUIProvider provider;

    Event(final ComponentsController components, final SwingUIProvider provider) {
        this.components = components;
        this.provider = provider;
    }
}