package io.github.spookylauncher.wrapper.tasks;

import io.github.spookylauncher.wrapper.SLWrapper;
import io.github.spookylauncher.wrapper.util.proxy.ProxyClassLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static io.github.spookylauncher.wrapper.util.log.Level.FATAL;
import static io.github.spookylauncher.wrapper.util.log.Logger.log;

public final class MinecraftClassSetup {
    public static final String LOG_ID = SLWrapper.LOG_ID + "/minecraft class setup";

    private Class<?> minecraftClass;

    public Class<?> getMinecraftClass() {
        return this.minecraftClass;
    }

    public boolean setup(ProxyClassLoader proxyClassLoader, Properties config) {
        String minecraftClassName = config.getProperty("minecraftClass", "net.minecraft.client.Minecraft");

        try {
            minecraftClass = proxyClassLoader.loadClass(minecraftClassName);
        } catch(ClassNotFoundException e) {
            log(FATAL, LOG_ID, "minecraft class (" + minecraftClassName + ") not found");
            log(FATAL, LOG_ID, e);
        } catch(Exception e) {
            log(FATAL, LOG_ID, "failed to load minecraft class (" + minecraftClassName + ")");
            log(FATAL, LOG_ID, e);
        }

        if(minecraftClass == null) return false;

        List<Integer> mcDirFields;

        if(config.containsKey("mcDirFieldIndex"))
            mcDirFields = Collections.singletonList(Integer.parseInt(config.getProperty("mcDirFieldIndex")));
        else mcDirFields = findMCDirectoryFields(minecraftClass);

        if(mcDirFields.isEmpty()) {
            log(FATAL, LOG_ID, "failed to detect \"minecraftDir\" field");
            return false;
        }

        File directory = new File(".");

        for(int field : mcDirFields) {
            if (!changeMCDirectory(minecraftClass, field, directory)) return false;
        }

        return true;
    }

    private List<Integer> findMCDirectoryFields(Class<?> clazz) {
        List<Integer> indices = new ArrayList<>();

        try {
            Field[] fs = clazz.getDeclaredFields();

            for(int i = 0;i < fs.length;i++) {
                fs[i].setAccessible(true);

                int mods = fs[i].getModifiers();

                if
                (
                        fs[i].getType() == File.class &&
                        Modifier.isStatic(mods) &&
                        Modifier.isPrivate(mods)
                ) indices.add(i);

                fs[i].setAccessible(false);
            }
        } catch(Exception e) {
            log(FATAL, LOG_ID, "failed to find minecraftDir field index: " + e);
        }

        return indices;
    }

    private boolean changeMCDirectory(Class<?> clazz, int fieldIndex, File directory) {
        try {
            Field f = clazz.getDeclaredFields()[fieldIndex];

            f.setAccessible(true);
            f.set(null, directory);
            f.setAccessible(false);

            return true;
        } catch(Exception e) {
            log(FATAL, LOG_ID, "failed to change minecraft directory: " + e);
            return false;
        }
    }
}