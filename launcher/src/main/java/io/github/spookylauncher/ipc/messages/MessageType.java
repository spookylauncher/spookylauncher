package io.github.spookylauncher.ipc.messages;

public enum MessageType {
    SELECT_VERSION(0),
    IPC_ERROR(1),

    FRAME_TOP_FRONT(2);

    public final int id;

    MessageType(int id) {
        this.id = id;
    }


    public static MessageType get(final int id) {
        for(final MessageType type : values()) {
            if(type.id == id) return type;
        }

        return null;
    }
}