package io.github.spookylauncher.advio;

import java.io.*;

public final class Resource {
    public static boolean exists(String resource) {
        try {
            try(final InputStream is = Resource.class.getResourceAsStream("/" + resource)) {
                if(is != null)
                    return true;
            }
        } catch(final IOException e) { e.printStackTrace(); }

        return false;
    }

    public static InputStream getInput(String resource) {
        return Resource.class.getResourceAsStream("/" + resource);
    }
}