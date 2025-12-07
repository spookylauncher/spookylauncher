package io.github.spookylauncher.components.ui;

public interface Button extends Label {

    void addActionEvent(Runnable rn);

    void setEnabled(boolean enabled);

    boolean isEnabled();
}