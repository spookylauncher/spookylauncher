package io.github.spookylauncher.components;

import io.github.spookylauncher.advio.AsyncOperation;
import io.github.spookylauncher.advio.IOUtils;
import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.components.ui.spi.UIProvider;
import io.github.spookylauncher.ipc.messages.MessageType;
import io.github.spookylauncher.protocol.ProtocolReader;
import io.github.spookylauncher.ipc.Constants;
import io.github.spookylauncher.ipc.messages.FrameTopFront;
import io.github.spookylauncher.ipc.messages.IpcError;
import io.github.spookylauncher.ipc.messages.SelectVersion;
import io.github.spookylauncher.tree.versions.VersionInfo;
import io.github.spookylauncher.util.Locale;
import io.mappedbus.MappedBusMessage;

import java.io.File;

public final class ProtocolHandler extends LauncherComponent {
    public ProtocolHandler(ComponentsController components) {
        super("Protocol Handler", components);
    }

    private boolean running;

    public void stop() {
        running = false;
    }

    @Override
    public void initialize() {
        super.initialize();

        IOUtils.deleteTree(new File(Constants.getFile()));

        AsyncOperation.run(
                () -> {
                    ProtocolReader reader = new ProtocolReader();

                    running = true;

                    try {
                        reader.open();

                        while(running) {
                            reader.read(ProtocolHandler.this);
                        }

                        reader.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void handle(MappedBusMessage msg) {
        final UIProvider uiProvider = components.get(UIProvider.class);
        final Locale locale = components.get(Translator.class).getLocale();

        log(Level.INFO, "handling IPC message: " + MessageType.get(msg.type()));

        if(msg instanceof SelectVersion) {
            String versionName = ( (SelectVersion) msg).versionName;

            final String repo = this.components.get(ManifestsURLs.class).getBaseDataURL();

            VersionInfo info = components.get(VersionsList.class).getManifest().getVersionInfo(repo, versionName);

            uiProvider.window().toTopFront();

            if(info != null) uiProvider.panel().setVersion(info);
            else {
                log(Level.ERROR, "version \"" + versionName + "\" specified in IPC message not found");
                uiProvider.messages().error(locale.get("error"), String.format(locale.get("versionNotFounded"), versionName));
            }
        } else if(msg instanceof IpcError) {
            IpcError err = (IpcError) msg;

            String errorMessage = String.format(locale.get(err.format), err.args);

            log(Level.ERROR, "ipc error: " + errorMessage);

            uiProvider.messages().error(locale.get("error"), "IPC: " + errorMessage);
        } else if(msg instanceof FrameTopFront) {
            uiProvider.window().toTopFront();
        } else {
            String msgType = msg.getClass().getSimpleName();

            log(Level.ERROR, "unsupported ipc message: " + msgType);

            uiProvider.messages().error(locale.get("error"), String.format(locale.get("ipcUnsupportedMsg"), msgType));
        }
    }
}