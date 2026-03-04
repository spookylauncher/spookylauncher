package io.github.spookylauncher.ui;

public interface Button extends Label {
    void addActionEvent(Runnable rn);

    void setEnabled(boolean enabled);

    boolean isEnabled();
}
