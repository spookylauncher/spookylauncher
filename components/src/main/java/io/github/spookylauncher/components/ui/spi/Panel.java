package io.github.spookylauncher.components.ui.spi;

import io.github.spookylauncher.util.Locale;

public interface Panel {

    Panel getChild(String name);
    void setEnabledButtons(boolean enabled, String...buttons);

    void setLocale(Locale locale);

    Locale getLocale();

    Button getButton(String button);

    void addActionEvent(String button, Runnable event);

    boolean isButtonEnabled(String button);

    void setEnabledButton(String button, boolean enabled);
}