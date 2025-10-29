package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.advio.collectors.URLCollector;
import io.github.spookylauncher.components.*;
import io.github.spookylauncher.components.ui.spi.Button;
import io.github.spookylauncher.components.ui.spi.Panel;
import io.github.spookylauncher.components.ui.spi.TitlePanel;
import io.github.spookylauncher.components.ui.swing.forms.TitlePanelForm;
import io.github.spookylauncher.tree.versions.VersionInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import io.github.spookylauncher.util.Locale;

import static io.github.spookylauncher.log.Level.ERROR;

class TitlePanelImpl extends LauncherComponent implements TitlePanel {

    private final Map<String, JButton> buttons = new HashMap<>();

    private final Map<String, Button> spiButtons = new HashMap<>();

    private final TitlePanelForm titlePanelForm;

    private VersionInfo currentVersion;

    private final BufferedImage noPreview;

    private Locale locale;

    TitlePanelImpl(final ComponentsController components, final JFrame frame) {
        super("Swing API Title Panel", components);

        this.noPreview = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);

        this.noPreview.getGraphics().drawString("=)", noPreview.getWidth() / 2, noPreview.getHeight() / 2);

        frame.setContentPane((titlePanelForm = new TitlePanelForm()).panel1);

        titlePanelForm.scrollPane.setBorder(null);
        titlePanelForm.scrollPane.setOpaque(false);

        this.buttons.put(PLAY, titlePanelForm.play);
        this.buttons.put(OPEN_ARTICLE, titlePanelForm.openArticle);
        this.buttons.put(SETTINGS, titlePanelForm.settings);
        this.buttons.put(VERSIONS, titlePanelForm.versions);

        setLocale(components.get(Translator.class).getLocale());

        setVersion(null);

        setEnabledButtons(false, PLAY, VERSIONS);
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public Button getButton(String button) {
        if(!spiButtons.containsKey(button)) {
            JButton jbutton = buttons.get(button);

            spiButtons.put(button, new Button() {
                @Override
                public void addActionEvent(Runnable rn) {
                    jbutton.addActionListener(e -> rn.run());
                }

                @Override
                public void setEnabled(boolean enabled) {
                    jbutton.setEnabled(enabled);
                }

                @Override
                public boolean isEnabled() {
                    return jbutton.isEnabled();
                }

                @Override
                public String getText() {
                    return jbutton.getText();
                }

                @Override
                public void setText(String text) {
                    jbutton.setText(text);
                }
            });
        }

        return spiButtons.get(button);
    }

    @Override
    public void addActionEvent(String button, Runnable event) {
        buttons.get(button).addActionListener(e -> event.run());
    }

    @Override
    public boolean isButtonEnabled(String button) {
        return buttons.get(button).isEnabled();
    }

    @Override
    public void setEnabledButtons(boolean enabled, String... buttons) {
        if(buttons == null || buttons.length == 0) {
            for(String button : this.buttons.keySet()) setEnabledButton(button, enabled);
        } else {
            for(String button : buttons) setEnabledButton(button, enabled);
        }
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        for(Map.Entry<String, JButton> entry : buttons.entrySet())
            entry.getValue().setText(locale.get(entry.getKey()));
    }

    @Override
    public void setEnabledButton(String button, boolean enabled) {
        buttons.get(button).setEnabled(enabled);
    }

    @Override
    public void setPreview(Image image) {
        titlePanelForm.preview.setIcon(new ImageIcon(
                image.getScaledInstance(500, 300, Image.SCALE_DEFAULT)));
    }

    @Override
    public void setVersion(VersionInfo info) {
        if(info == null) {
            titlePanelForm.versionName.setText("...");
            titlePanelForm.releaseDate.setText("");
            titlePanelForm.credit.setText("");
            titlePanelForm.openArticle.setEnabled(false);
            setPreview(noPreview);
            titlePanelForm.play.setEnabled(false);
            return;
        }

        currentVersion = info;

        VersionsList versions = components.get(VersionsList.class);

        titlePanelForm.versionName.setText(info.name);

        titlePanelForm.releaseDate.setText(info.releaseDate.toString());

        titlePanelForm.credit.setText(locale.get("developer") + ": " + info.developer);

        titlePanelForm.openArticle.setEnabled(true);

        titlePanelForm.play.setEnabled(true);

        titlePanelForm.play.setText(versions.isInstalled(info) ? locale.get("play") : locale.get("install"));

        new Thread(() -> {
            if(info.getPreviewsCount() > 0) {
                try {
                    setPreview(new URLCollector
                            (
                                    components.get(ManifestsURLs.class).getBaseDataURL() + "/versions/"
                                    + info.name
                                    + "/previews/preview_"
                                    + (new Random().nextInt(info.getPreviewsCount()) + 1) + ".png"
                            ).collectImage());
                } catch (IOException | URISyntaxException e) {
                    log(ERROR, "failed to set preview");
                    log(ERROR, e);
                }
            } else setPreview(noPreview);
        }).start();

        String lang = locale.getLanguage();

        boolean fallback = true;

        for(String label : info.labels) {
            if(lang.equals(label)) {
                fallback = false;
                break;
            }
        }

        final String repo = this.components.get(ManifestsURLs.class).getBaseDataURL();

        final String labelUrl = repo + "/versions/" + info.name + "/label" + (fallback ? "" : "_" + lang) + ".txt";

        new Thread(
                () -> {
                    try {
                        titlePanelForm.description.setText(
                                new URLCollector(labelUrl).collectString()
                        );
                    } catch (IOException | URISyntaxException e) {
                        log(ERROR, "failed to set description");
                        log(ERROR, e);
                    }
                }
        ).start();
    }

    @Override
    public VersionInfo getCurrentVersion() {
        return this.currentVersion;
    }

    @Override
    public Panel getChild(String name) {
        return null;
    }
}