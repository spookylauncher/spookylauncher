package io.github.spookylauncher.components;

import io.github.spookylauncher.components.ui.UIProvider;
import io.github.spookylauncher.components.ui.Messages;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.StringUtils;

import java.net.UnknownHostException;

public final class ErrorHandler extends LauncherComponent {
    public ErrorHandler(ComponentsController components) {
        super("Error Handler", components);
    }

    public void handleException(String titleKey, Exception ex) {
        Locale locale = components.get(Translator.class).getLocale();

        String message;

        if(ex instanceof UnknownHostException) message = get(locale, "checkConnection", "Check your internet connection");
        else message = String.format(get(locale, "error", "Error") + ": %s\n" + get(locale, "stacktrace", "Stacktrace") + ": %s", ex.getMessage(), StringUtils.getStackTrace(ex));

        final Messages messages = components.get(UIProvider.class).messages();

        messages.error(get(locale, titleKey, titleKey), message);
    }

    private String get(Locale locale, String key, String def) {
        if(locale == null) return def;
        else return locale.get(key);
    }
}