package io.github.spookylauncher.components.ui.spi;

import io.github.spookylauncher.tree.versions.VersionInfo;

import java.awt.Image;
import java.awt.image.BufferedImage;

public interface TitlePanel extends Panel {

    String
    VERSIONS = "versions",
    PLAY = "play",
    OPEN_ARTICLE = "openArticle",
    SETTINGS = "settings";

    void setPreview(Image image);

    void setVersion(VersionInfo info);

    VersionInfo getCurrentVersion();
}