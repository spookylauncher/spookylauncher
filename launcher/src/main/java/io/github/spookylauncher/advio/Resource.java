package io.github.spookylauncher.advio;

import io.github.spookylauncher.advio.collectors.Collector;
import io.github.spookylauncher.advio.assembly.Assembly;
import io.github.spookylauncher.advio.assembly.AssemblyIO;
import io.github.spookylauncher.advio.assembly.Blob;

import java.io.*;
import java.util.*;

public final class Resource {
    private static final List<Assembly> assemblies = new ArrayList<>();

    public static boolean exists(String resource) {
        try {
            try(final InputStream is = Resource.class.getResourceAsStream("/" + resource)) {
                if(is != null)
                    return true;
            }
        } catch(final IOException e) {
            return false;
        }

        return getBlob(resource) != null;
    }

    public static InputStream getInput(String resource) {
        InputStream in = Resource.class.getResourceAsStream("/" + resource);

        if(in != null) return in;
        else {
            Blob blob = getBlob(resource);

            if(blob != null) return new ByteArrayInputStream(blob.data);
            else return null;
        }
    }

    private static Blob getBlob(String name) {
        if(name.startsWith("/")) name = name.substring(1);

        for(Assembly assembly : assemblies) {
            if(assembly.containsBlob(name)) return assembly.getBlob(name);
        }

        return null;
    }

    public static void putAssembly(Collector collector) {
        putAssembly(collector.collectInput());
    }

    public static void putAssembly(InputStream in) {
        putAssembly(AssemblyIO.read(in));
    }

    public static void putAssembly(Assembly assembly) {
        assemblies.add(assembly);
    }
}