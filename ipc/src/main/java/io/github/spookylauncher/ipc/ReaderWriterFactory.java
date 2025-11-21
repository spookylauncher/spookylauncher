package io.github.spookylauncher.ipc;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;

public final class ReaderWriterFactory {
    public static MappedBusReader createReader() {
        return new MappedBusReader(Constants.getFile(), Constants.FILE_SIZE, Constants.RECORD_SIZE);
    }

    public static MappedBusWriter createWriter() {
        return new MappedBusWriter(Constants.getFile(), Constants.FILE_SIZE, Constants.RECORD_SIZE);
    }
}