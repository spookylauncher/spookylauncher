package io.github.spookylauncher.ipc.messages;

import java.io.*;

public class IpcError extends AdvancedMappedBusMessage {
    public String format;
    public Object[] args;

    @Override
    public int type() {
        return MessageType.IPC_ERROR.id;
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        format = in.readUTF();
        args = new Object[in.readInt()];

        for(int i = 0; i < args.length; i++) args[i] = in.readUTF();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(format);
        out.writeInt(args.length);

        for(Object arg : args) out.writeUTF(String.valueOf(arg));
    }
}