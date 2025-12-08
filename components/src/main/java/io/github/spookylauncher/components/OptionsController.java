package io.github.spookylauncher.components;

import io.github.spookylauncher.tree.launcher.Options;
import io.github.spookylauncher.io.collectors.FileCollector;
import io.github.spookylauncher.io.peddlers.FilePeddler;
import io.github.spookylauncher.util.Json;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class OptionsController extends LauncherComponent {
    private static final Logger LOG = Logger.getLogger("options controller");

    private Options options;

    private final File optionsFile;

    public OptionsController(ComponentsController components, File optionsFile) {
        super("Options Controller", components);
        this.optionsFile = optionsFile;
    }

    public Options getOptions() { return this.options; }

    public void load() throws IOException {
        LOG.info("loading options");
        options = Json.collectJson(new FileCollector(optionsFile), Options.class);
    }

    public void store() throws IOException {
        store(true);
    }

    public void store(boolean canShowRpc) throws IOException {
        LOG.info("saving options");

        Json.peddleJson(new FilePeddler(optionsFile), options);

        DiscordPresenceViewer discordRpc = components.get(DiscordPresenceViewer.class);

        if(canShowRpc) {
            if(options.discordPresence) discordRpc.showMenuPresence();
            else discordRpc.clearPresence();
        }
    }

    public void initialize() throws IOException {
        super.initialize();

        if(optionsFile.exists()) load();
        else {
            options = new Options();
            store(false);
        }
    }
}