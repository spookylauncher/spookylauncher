package io.github.spookylauncher.ipc.messages;

import java.io.*;

public class SelectVersion extends AdvancedMappedBusMessage {
    public String versionName;

    @Override
    public int type() {
        return MessageType.SELECT_VERSION.id;
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.versionName = in.readUTF();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.versionName);
    }
}