package io.github.spookylauncher.protocol;

import io.github.spookylauncher.components.ProtocolHandler;
import io.github.spookylauncher.ipc.ReaderWriterFactory;
import io.github.spookylauncher.ipc.messages.IpcError;
import io.github.spookylauncher.ipc.messages.MessageType;
import io.github.spookylauncher.ipc.messages.MessagesFactory;
import io.github.spookylauncher.ipc.messages.SelectVersion;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MappedBusWriter;

import java.io.IOException;

public final class ProtocolSender {
    private final MappedBusWriter writer;

    private final boolean directlySending;

    private final ProtocolHandler handler;

    public ProtocolSender() {
        this(null);
    }

    public ProtocolSender(ProtocolHandler handler) {
        this.directlySending = handler != null;
        this.writer = !directlySending ? ReaderWriterFactory.createWriter() : null;
        this.handler = handler;
    }

    public void open() throws IOException { if(!directlySending) writer.open(); }

    public void close() throws IOException { if(!directlySending) writer.close(); }

    public void sendFrameTopFront() {
        sendMessage(MessagesFactory.getOrCreateNewInstance(MessageType.FRAME_TOP_FRONT.id));
    }
    public void sendIpcError(String format, String...args) {
        IpcError msg = MessagesFactory.getOrCreateNewInstance(MessageType.IPC_ERROR.id);

        msg.format = format;
        msg.args = args;

        sendMessage(msg);
    }
    public void sendSelectVersion(String versionName) {
        SelectVersion msg = MessagesFactory.getOrCreateNewInstance(MessageType.SELECT_VERSION.id);
        msg.versionName = versionName;
        sendMessage(msg);
    }

    private void sendMessage(MappedBusMessage msg) {
        if(!directlySending) {
            try {
                writer.write(msg);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else handler.handle(msg);
    }
}