package io.github.spookylauncher.protocol;

import io.github.spookylauncher.components.ProtocolHandler;
import io.github.spookylauncher.ipc.ReaderWriterFactory;
import io.github.spookylauncher.ipc.messages.MessagesFactory;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MappedBusReader;

import java.io.IOException;

public final class ProtocolReader {
    private final MappedBusReader reader = ReaderWriterFactory.createReader();

    public void open() throws IOException { reader.open(); }

    public void close() throws IOException { reader.close(); }

    public void read(ProtocolHandler handler) {
        try {
            if(reader.next()) {
                MappedBusMessage msg = MessagesFactory.getOrCreateNewInstance(reader.readType());

                reader.readMessage(msg);

                handler.handle(msg);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}