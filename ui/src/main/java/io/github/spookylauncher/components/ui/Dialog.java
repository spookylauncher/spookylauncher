package io.github.spookylauncher.components.ui;

public interface Dialog extends Label {
    String getTitle();

    void setTitle(String title);

    void setVisible(boolean visible);
}