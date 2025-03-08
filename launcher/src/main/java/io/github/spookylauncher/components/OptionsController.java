package io.github.spookylauncher.components;

import io.github.spookylauncher.components.log.Level;
import io.github.spookylauncher.tree.launcher.Options;
import io.github.spookylauncher.advio.collectors.FileCollector;
import io.github.spookylauncher.advio.peddlers.FilePeddler;
import io.github.spookylauncher.util.io.Json;

import java.io.File;

public final class OptionsController extends LauncherComponent {
    private Options options;

    private final File optionsFile;

    public OptionsController(ComponentsController components, File optionsFile) {
        super("Options Controller", components);
        this.optionsFile = optionsFile;
    }

    public Options getOptions() { return this.options; }

    public void load() {
        log(Level.INFO, "loading options");
        options = Json.collectJson(new FileCollector(optionsFile), Options.class);
    }

    public void store() {
        store(true);
    }
    public void store(boolean canShowRpc) {
        log(Level.INFO, "saving options");

        Json.peddleJson(new FilePeddler(optionsFile), options);

        DiscordPresenceViewer discordRpc = components.get(DiscordPresenceViewer.class);

        if(canShowRpc) {
            if(options.discordPresence) discordRpc.showMenuPresence();
            else discordRpc.clearPresence();
        }
    }

    public void initialize() {
        super.initialize();

        if(optionsFile.exists()) load();
        else {
            options = new Options();
            store(false);
        }
    }
}