package io.github.spookylauncher.components;

import io.github.spookylauncher.components.events.EventsManager;
import io.github.spookylauncher.components.events.Events;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.util.Locale;
import net.arikia.dev.drpc.*;

import java.io.IOException;
import java.util.logging.Logger;

public final class DiscordPresenceViewer extends LauncherComponent {
    private static final Logger LOG = Logger.getLogger("discord presence viewer");
    private static final String APP_ID = "1264356894361780286";

    public DiscordPresenceViewer(ComponentsController components) {
        super("Discord Presence Viewer", components);
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        new Thread(
                () -> {
                    try {
                        components.get(EventsManager.class).subscribe(Events.SHUTDOWN, args -> {
                            active = false;
                            LOG.info("disconnecting from discord client...");
                            DiscordRPC.discordShutdown();
                        });

                        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                                .setReadyEventHandler(user -> LOG.info("successfully connected to discord client"))
                                .setErroredEventHandler((code, message) -> {
                                    LOG.severe("error occurred in Discord RPC: #" + code + ": " + message);
                                })
                                .setDisconnectedEventHandler((code, message) -> {
                                            LOG.info("disconnected from discord client: #" + code + ": " + message);
                                        }
                                )
                                .build();

                        DiscordRPC.discordInitialize(APP_ID, handlers, true);

                        active = true;

                        while(active) {
                            DiscordRPC.discordRunCallbacks();
                            Thread.sleep(100L); // reducing the load on the processor
                        }
                    } catch(final Exception e) {
                        LOG.throwing("io.github.spookylauncher.components.DiscordPresenceViewer", "initialize", e);
                    }
                }
        ).start();

        if(components.get(OptionsController.class).getOptions().discordPresence) components.get(DiscordPresenceViewer.class).showMenuPresence();
    }

    public void showMenuPresence() {
        LOG.info("switching to menu rich presence");

        DiscordRPC.discordUpdatePresence(
                new DiscordRichPresence.Builder(components.get(Translator.class).getLocale().get("inMenu"))
                        .setBigImage("rich-presence", "Spooky Launcher")
                        .setStartTimestamps(System.currentTimeMillis())
                        .build()
        );
    }

    public void showGamePresence(VersionInfo version) {
        LOG.info("switching to game rich presence (version: " + version.name + ")");

        Locale locale = components.get(Translator.class).getLocale();

        String nickname = components.get(OptionsController.class).getOptions().getNickname();

        if(nickname.isEmpty()) nickname = "?";

        DiscordRPC.discordUpdatePresence(
                new DiscordRichPresence.Builder(locale.get("nickname") + ": " + nickname)
                        .setBigImage("rich-presence", "Spooky Launcher")
                        .setDetails(locale.get("playingIn") + ": " + version.name)
                        .setStartTimestamps(System.currentTimeMillis())
                        .build()
        );
    }

    public void clearPresence() {
        DiscordRPC.discordClearPresence();
    }

    private boolean active;
}