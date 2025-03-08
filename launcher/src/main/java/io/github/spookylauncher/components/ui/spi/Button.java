package io.github.spookylauncher.components.ui.spi;

public interface Button extends Label {

    void addActionEvent(Runnable rn);

    void setEnabled(boolean enabled);

    boolean isEnabled();
}