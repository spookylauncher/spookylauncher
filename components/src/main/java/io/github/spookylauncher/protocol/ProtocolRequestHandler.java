package io.github.spookylauncher.protocol;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.components.ProtocolHandler;
import io.github.spookylauncher.util.uri.QueryParser;
import io.github.spookylauncher.util.StringUtils;
import io.github.spookylauncher.components.VersionsList;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.spookylauncher.log.Logger.log;

public final class ProtocolRequestHandler {
    private static final String LOG_ID = "protocol request handler";

    public static Consumer<ComponentsController> createConsumer(final String uriStr, final boolean hasActiveLauncherInstance) throws UnsupportedEncodingException, URISyntaxException {
        log(Level.INFO, LOG_ID,"parsing request");

        log(Level.DEBUG, LOG_ID, "parsing uri");

        final URI uri = new URI(uriStr);

        log(Level.DEBUG, LOG_ID, "parsing query");

        final Map<String, String> query = QueryParser.parse(uri.getRawQuery());

        log(Level.DEBUG, LOG_ID, "uri: " + uri);
        log(Level.DEBUG, LOG_ID, "raw query: " + uri.getRawQuery());
        log(Level.DEBUG, LOG_ID, "query: " + StringUtils.valueOf(query));

        return
        (c) -> {
            ProtocolSender sender = hasActiveLauncherInstance ? new ProtocolSender() : new ProtocolSender(c.get(ProtocolHandler.class));

            Runnable sendTask = () -> {
                try {
                    sender.open();

                    if(!query.containsKey("command")) sender.sendIpcError("ipcNoCommandParam");
                    else {
                        String rawCommand = query.get("command");

                        CommandType type = CommandType.parse(rawCommand);

                        if(type == null) sender.sendIpcError("ipcUnknownCmd", rawCommand);
                        else if (type == CommandType.SELECT_VERSION) {
                            final String fragment = uri.getFragment();

                            if (fragment == null) sender.sendIpcError("ipcFragmentExpected");
                            else sender.sendSelectVersion(fragment);
                        }
                    }
                    sender.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            };

            if(hasActiveLauncherInstance) sendTask.run();
            else c.get(VersionsList.class).addOnDownloadedEvent(() -> {
                sendTask.run();
                return true;
            });
        };
    }
}