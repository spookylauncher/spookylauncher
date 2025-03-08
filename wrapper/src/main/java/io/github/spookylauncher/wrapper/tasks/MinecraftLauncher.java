package io.github.spookylauncher.wrapper.tasks;

import io.github.spookylauncher.wrapper.SLWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.github.spookylauncher.wrapper.util.log.Level.FATAL;
import static io.github.spookylauncher.wrapper.util.log.Level.INFO;
import static io.github.spookylauncher.wrapper.util.log.Logger.log;

public final class MinecraftLauncher {
    public static final String LOG_ID = SLWrapper.LOG_ID + "/minecraft launcher";

    public boolean launch(Class<?> minecraftClass, Properties config) {
        log(INFO, LOG_ID, "starting game");

        try {
            Method main = minecraftClass.getDeclaredMethod("main", String[].class);

            main.setAccessible(true);

            List<String> argsList = new ArrayList<>();

            String nickname = config.getProperty("nickname");

            if(nickname != null && !nickname.isEmpty()) argsList.add(nickname);

            main.invoke(null, new Object[] { argsList.toArray(new String[0]) });

            return true;
        } catch(Exception e) {
            log(FATAL, LOG_ID, "failed to launch game: " + e);
            return false;
        }
    }
}