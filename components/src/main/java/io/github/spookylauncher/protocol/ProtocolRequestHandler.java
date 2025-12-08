package io.github.spookylauncher.protocol;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.ProtocolHandler;
import io.github.spookylauncher.util.uri.QueryParser;
import io.github.spookylauncher.util.StringUtils;
import io.github.spookylauncher.components.VersionsList;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class ProtocolRequestHandler {
    private static final Logger LOG = Logger.getLogger("protocol request handler");

    public static Consumer<ComponentsController> createConsumer(final String uriStr, final boolean hasActiveLauncherInstance) throws UnsupportedEncodingException, URISyntaxException {
        LOG.info("parsing request");

        LOG.info("parsing uri");

        final URI uri = new URI(uriStr);

        LOG.fine("parsing query");

        final Map<String, String> query = QueryParser.parse(uri.getRawQuery());

        LOG.fine("uri: " + uri);
        LOG.fine("raw query: " + uri.getRawQuery());
        LOG.fine("query: " + StringUtils.valueOf(query));

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