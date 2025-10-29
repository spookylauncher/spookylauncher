package io.github.spookylauncher.advio;

import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.log.Logger;

import java.io.*;

public final class Resource {
    private static final String LOG_ID = "resources provider";

    public static boolean exists(String resource) {
        try {
            try(final InputStream is = Resource.class.getResourceAsStream("/" + resource)) {
                if(is != null)
                    return true;
            }
        } catch(final IOException e) {
            Logger.log(Level.ERROR, LOG_ID, e);
        }

        return false;
    }

    public static InputStream getInput(String resource) {
        return Resource.class.getResourceAsStream("/" + resource);
    }
}