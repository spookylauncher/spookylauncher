package io.github.spookylauncher.wrapper.security;

import java.util.*;

public final class NetworkChecker extends Checker {

    private final Map<String, Set<Integer>> hosts;

    public NetworkChecker(Collection<String> hosts) {
        this(CheckMode.PERMIT_ALL_EXCEPT, hosts);
    }

    public NetworkChecker(CheckMode mode, Collection<String> hosts) {
        super(mode);

        Map<String, Set<Integer>> map = new HashMap<>();

        for(String host : hosts)
            map.put(host, null);

        this.hosts = Collections.unmodifiableMap(map);
    }

    public NetworkChecker(Map<String, Set<Integer>> hosts) {
        this(CheckMode.PERMIT_ALL_EXCEPT, hosts);
    }

    public NetworkChecker(CheckMode mode, Map<String, Set<Integer>> hosts) {
        super(mode);

        hosts.replaceAll((k, v) -> Collections.unmodifiableSet(v));

        this.hosts = Collections.unmodifiableMap(hosts);
    }

    public void checkConnect(String host, int port) {
        if(hosts.containsKey(host)) {
            Set<Integer> ports = hosts.get(host);
            
            if((mode == CheckMode.PERMIT_ALL_EXCEPT) == (ports == null || ports.contains(port)))
                throw new SecurityException("not permitted host \"" + host + "\" or port \"" + port + "\"");

        }
    }
}