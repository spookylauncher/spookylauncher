package io.github.spookylauncher.components.ui.swing.beauty;

import io.github.spookylauncher.io.Resource;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UIBeautician {

    private static final Logger LOG = Logger.getLogger("swing ui");

    private static final String MINECRAFTIA_FONT = "Minecraftia-Regular.ttf";
    private static final String[] FONTS = {
            MINECRAFTIA_FONT
    };
    private static final Map<String, Font> loadedFonts = new HashMap<>();

    public static void comb(Component component) {

        if(component instanceof Container) {
            if(component instanceof JComponent) {
                combSwing((JComponent) component);

                if(component instanceof JButton) {
                    combSwing((JButton) component);
                } else if(component instanceof JLabel) {
                    combSwing((JLabel) component);
                }
            }

            if(component instanceof JDialog) {
                combSwing((JDialog) component);
            }

            for(Component c : ((Container) component).getComponents())
                comb(c);
        }
    }

    public static void combSwing(JComponent component) {
        component.setFont(loadedFonts.get(MINECRAFTIA_FONT));
    }

    public static void combSwing(JButton button) {

    }

    public static void combSwing(JLabel label) {

    }

    public static void combSwing(JDialog dialog) {
        comb(dialog.getContentPane());
    }

    static {
        for(String fontPath : FONTS) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, Resource.getInput(fontPath));

                font = font.deriveFont(13.5f);

                loadedFonts.put(fontPath, font);
            } catch(Exception e) {
                LOG.severe("failed to load font \"" + fontPath + "\"");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ui.swing.beauty.UIBeautician", "<clinit>", "THROW", e);
            }
        }
    }
}