package io.github.spookylauncher.components;

import io.github.spookylauncher.components.log.Level;
import io.github.spookylauncher.components.log.Logger;

public abstract class LauncherComponent {
    protected final ComponentsController components;

    private String name;

    private String logId;

    public LauncherComponent(final String name, final ComponentsController components) {
        this.components = components;
        this.setName(name);
    }

    public void log(final Object msg) {
        log(Level.INFO, msg);
    }

    public void log(final Level level, final Object msg) {
        Logger.log(level, logId, msg);
    }

    protected void setName(final String name) {
        this.name = name;
        this.logId = this.name.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    public void initialize() { }
}