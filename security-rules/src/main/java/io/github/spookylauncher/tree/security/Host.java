package io.github.spookylauncher.tree.security;

import java.util.HashSet;

public final class Host {
    public String name;
    private int port = -1;
    private HashSet<Integer> ports;

    public HashSet<Integer> getPorts() {
        if(port != -1) {
            ports.add(port);
            port = -1;
        }

        return ports;
    }
}