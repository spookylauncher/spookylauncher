package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.io.Resource;
import io.github.spookylauncher.util.structures.tuple.Tuple2;

import java.awt.*;
import java.util.logging.*;

public final class AssetsLoader {

    private static final Logger LOG = Logger.getLogger("swing ui");

    // { font-path, font-size }
    private static final String[] FONTS = {
            "Minecraftia-Regular.ttf"
    };

    public static void load() {
        for(String fontPath : FONTS) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, Resource.getInput(fontPath));

                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

                LOG.info(String.format("font \"%s\" successfully loaded and registered", font.getName()));
            } catch(Exception e) {
                LOG.severe("failed to load font \"" + fontPath + "\"");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ui.swing.FontLoader", "loadFonts", "THROW", e);
            }
        }
    }
}