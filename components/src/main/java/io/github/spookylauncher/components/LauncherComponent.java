package io.github.spookylauncher.components;

import java.io.IOException;

public abstract class LauncherComponent {
    protected final ComponentsController components;

    private String name;

    public LauncherComponent(final String name, final ComponentsController components) {
        this.components = components;
        this.setName(name);
    }

    protected void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void initialize() throws IOException { }
}