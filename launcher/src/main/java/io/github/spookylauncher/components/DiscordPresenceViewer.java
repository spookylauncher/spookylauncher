package io.github.spookylauncher.components;

import io.github.spookylauncher.components.events.Events;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.advio.AsyncOperation;
import io.github.spookylauncher.util.Locale;
import net.arikia.dev.drpc.*;
import static io.github.spookylauncher.log.Level.*;

public final class DiscordPresenceViewer extends LauncherComponent {
    private static final String APP_ID = "1264356894361780286";

    public DiscordPresenceViewer(ComponentsController components) {
        super("Discord Presence Viewer", components);
    }

    @Override
    public void initialize() {
        super.initialize();

        AsyncOperation.run(
                () -> {
                    try {
                        components.get(Events.class).subscribe(Events.SHUTDOWN, args -> {
                            active = false;
                            log(INFO, "disconnecting from discord client...");
                            DiscordRPC.discordShutdown();
                        });

                        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                                .setReadyEventHandler(user -> log(INFO, "successfully connected to discord client"))
                                .setErroredEventHandler((code, message) -> {
                                    log(ERROR, "error occurred in Discord RPC: #" + code + ": " + message);
                                })
                                .setDisconnectedEventHandler((code, message) -> {
                                            log(INFO, "disconnected from discord client: #" + code + ": " + message);
                                        }
                                )
                                .build();

                        DiscordRPC.discordInitialize(APP_ID, handlers, true);

                        active = true;

                        while(active) {
                            DiscordRPC.discordRunCallbacks();
                            Thread.sleep(100L);
                        }
                    } catch(final Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        if(components.get(OptionsController.class).getOptions().discordPresence) components.get(DiscordPresenceViewer.class).showMenuPresence();
    }

    public void showMenuPresence() {
        log(INFO, "switching to menu rich presence");

        DiscordRPC.discordUpdatePresence(
                new DiscordRichPresence.Builder(components.get(Translator.class).getLocale().get("inMenu"))
                        .setBigImage("rich-presence", "Spooky Launcher")
                        .setStartTimestamps(System.currentTimeMillis())
                        .build()
        );
    }

    public void showGamePresence(VersionInfo version) {
        log(INFO, "switching to game rich presence (version: " + version.name + ")");

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