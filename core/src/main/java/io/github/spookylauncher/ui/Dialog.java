package io.github.spookylauncher.ui;

public interface Dialog extends Label {
    String getTitle();

    void setTitle(String title);

    void setVisible(boolean visible);
}
