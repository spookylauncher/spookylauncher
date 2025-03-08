package io.github.spookylauncher.wrapper.security.file;

import java.util.*;

public final class FileAccessType {
    public static final int
    READ = 0b1,
    WRITE = 0b10,
    DELETE = 0b100,
    EXECUTE = 0b1000,
    READLINK = 0b10000;

    private static final Map<Integer, String> names = new HashMap<>();

    static {
        names.put(READ, "read");
        names.put(WRITE, "write");
        names.put(DELETE, "delete");
        names.put(EXECUTE, "execute");
        names.put(READLINK, "readlink");
    }

    public static int getMaskFromFilePermissionActions(String actions) {
        actions = actions.replace(" ", "");

        String[] names = actions.contains(",") ? actions.split(",") : new String[] { actions };

        int mask = 0;

        for(String name : names)
            mask = mask | getMask(name);

        return mask;
    }

    public static int getMask(String name) {
        for(Map.Entry<Integer, String> entry : names.entrySet()) {
            if(entry.getValue().equals(name)) return entry.getKey();
        }

        return 0;
    }

    public static String getName(int type) {
        if(names.containsKey(type)) return names.get(type);
        else throw new IllegalArgumentException("unknown type: " + type);
    }

    public static boolean canRead(int access) {
        return (access & READ) != 0;
    }

    public static boolean canWrite(int access) {
        return (access & WRITE) != 0;
    }

    public static boolean canDelete(int access) {
        return (access & DELETE) != 0;
    }

    public static boolean canExecute(int access) { return (access & EXECUTE) != 0; }

    public static boolean canReadlink(int access) { return (access & READLINK) != 0; }
}