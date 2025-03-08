package io.github.spookylauncher.ipc.messages;

import io.mappedbus.MappedBusMessage;

import java.util.HashMap;

public final class MessagesFactory {
    private static final HashMap<Integer, Class<? extends MappedBusMessage>> messagesClassesMap = new HashMap<>();
    private static final HashMap<Integer, MappedBusMessage> messagesInstancesMap = new HashMap<>();

    public static <T extends MappedBusMessage> T getOrCreateNewInstance(int type) {
        MappedBusMessage msg = messagesInstancesMap.get(type);

        if(msg == null) {
            Class<? extends MappedBusMessage> clazz = messagesClassesMap.get(type);

            if(clazz != null) {
                try {
                    msg = clazz.newInstance();

                    messagesInstancesMap.put(type, msg);

                    return (T) msg;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else throw new UnsupportedOperationException("Unsupported message type: " + type);
        } else return (T) msg;
    }

    static {
        messagesClassesMap.put(MessageType.SELECT_VERSION.id, SelectVersion.class);
        messagesClassesMap.put(MessageType.IPC_ERROR.id, IpcError.class);
        messagesClassesMap.put(MessageType.FRAME_TOP_FRONT.id, FrameTopFront.class);
    }
}