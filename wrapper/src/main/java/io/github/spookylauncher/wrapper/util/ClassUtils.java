package io.github.spookylauncher.wrapper.util;

import java.io.IOException;
import java.io.InputStream;

public final class ClassUtils {
    public static boolean classExists(Class<?> clazz, String name) {
        return classExists(clazz.getClassLoader(), name);
    }

    public static boolean classExists(ClassLoader loader, String name) {
        return resourceExists(loader, name.replace(".", "/") + ".class");
    }

    public static boolean resourceExists(Class<?> clazz, String path) {
        return resourceExists(clazz.getClassLoader(), path);
    }
    public static boolean resourceExists(ClassLoader loader, String path) {
        InputStream in = loader.getResourceAsStream(path);

        if(in != null) {
            try {
                in.close();
            } catch(IOException e) {
                e.printStackTrace();
            }

            return true;
        } else return false;
    }
}