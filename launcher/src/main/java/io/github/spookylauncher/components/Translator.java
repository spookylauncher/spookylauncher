package io.github.spookylauncher.components;

import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.io.Resource;
import io.github.spookylauncher.io.collectors.ResourceCollector;

import java.io.IOException;
import java.util.Properties;

import static io.github.spookylauncher.log.Level.*;

public final class Translator extends LauncherComponent {
    private Locale locale;

    public Translator(ComponentsController components) {
        super("Translator", components);
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        this.reloadLocale();
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, String def) {
        String translated = locale.get(key);

        return translated == null ? def : translated;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void reloadLocale() {
        log(INFO, "locale loading started");

        String localeResource = "locales/" + components.get(OptionsController.class).getOptions().locale + ".properties";

        if(!Resource.exists(localeResource)) localeResource =  "locales/fallback.properties";

        Properties props = new Properties();

        try {
            props.load(new ResourceCollector(localeResource).collectReader());
            log(INFO, "locale successfully loaded");
        } catch(Exception e) {
            log(INFO, "failed to load locale");
            components.get(ErrorHandler.class).handleException("Failed to load locale", e);
        }

        locale = new Locale(components.get(OptionsController.class).getOptions().locale, props);
    }
}