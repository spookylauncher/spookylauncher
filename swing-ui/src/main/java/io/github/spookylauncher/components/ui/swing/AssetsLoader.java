package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.io.Resource;
import io.github.spookylauncher.util.structures.tuple.Tuple2;

import java.awt.*;
import java.util.logging.*;

public final class AssetsLoader {

    private static final Logger LOG = Logger.getLogger("swing ui");

    // { font-path, font-size }
    private static final Tuple2<String, Float>[] FONTS = new Tuple2[]{
            new Tuple2<>("Minecraftia-Regular.ttf", 13.5f)
    };

    public static void load() {
        for(Tuple2<String, Float> fontData : FONTS) {
            try {
                Font font = Font.createFont(
                        Font.TRUETYPE_FONT, Resource.getInput(fontData.x)
                );

                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(
                        font.deriveFont(fontData.y)
                );

                LOG.info(String.format("font \"%s\" successfully loaded and registered", font.getName()));
            } catch(Exception e) {
                LOG.severe("failed to load font \"" + fontData.x + "\"");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ui.swing.FontLoader", "loadFonts", "THROW", e);
            }
        }
    }
}