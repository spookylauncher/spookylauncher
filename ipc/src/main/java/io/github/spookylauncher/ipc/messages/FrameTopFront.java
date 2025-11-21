package io.github.spookylauncher.ipc.messages;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class FrameTopFront implements MappedBusMessage {

    @Override
    public void write(MemoryMappedFile mem, long pos) {}

    @Override
    public void read(MemoryMappedFile mem, long pos) {}

    @Override
    public int type() {
        return MessageType.FRAME_TOP_FRONT.id;
    }
}