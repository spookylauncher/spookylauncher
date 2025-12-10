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
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(
                        Font.createFont(
                                Font.TRUETYPE_FONT, Resource.getInput(fontData.x)
                        ).deriveFont(fontData.y)
                );
            } catch(Exception e) {
                LOG.severe("failed to load font \"" + fontData.x + "\"");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ui.swing.FontLoader", "loadFonts", "THROW", e);
            }
        }
    }
}