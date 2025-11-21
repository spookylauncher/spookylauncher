package io.github.spookylauncher.ipc.messages;

import io.mappedbus.*;

import java.io.*;

public abstract class AdvancedMappedBusMessage implements MappedBusMessage {
    @Override
    public void write(MemoryMappedFile memoryMappedFile, long l) {
        try {
            DataOutputStream out =
                    new DataOutputStream(
                            new OutputStream() {
                                private long index = l;

                                @Override
                                public void write(int b) {
                                    memoryMappedFile.putByte(index++, (byte) b);
                                }
                            }
                    );

            write(out);

            out.close();
        } catch(IOException e) {
            System.err.println("Failed to write message with type " + type() + ": ");
            e.printStackTrace();
        }
    }

    @Override
    public void read(MemoryMappedFile memoryMappedFile, long l) {
        try {
            DataInputStream in =
                    new DataInputStream(
                            new InputStream() {
                                private long index = l;

                                @Override
                                public int read() {
                                    return memoryMappedFile.getByte(index++);
                                }
                            }
                    );

            read(in);

            in.close();
        } catch(IOException e) {
            System.err.println("Failed to read message with type " + type() + ": ");
            e.printStackTrace();
        }
    }

    public abstract void read(DataInputStream in) throws IOException;

    public abstract void write(DataOutputStream out) throws IOException;
}