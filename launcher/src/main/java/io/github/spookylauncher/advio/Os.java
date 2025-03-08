package io.github.spookylauncher.advio;

public enum Os {
    WINDOWS("Windows", "win"),
    LINUX("Linux", "linux", "unix", "solaris"),
    MACOS("MacOS","mac");

    public final String[] patterns;
    public final String name;

    public static final Os CURRENT = getOsByName(System.getProperty("os.name"));

    Os(String name, String... patterns) {
        this.name = name;
        this.patterns = patterns;
    }

    public static Os getOsByName(String name) {
        name = name.toLowerCase();

        for(Os os : Os.values()) {
            for(String pattern : os.patterns) {
                if(name.contains(pattern)) return os;
            }
        }

        throw new RuntimeException("Unknown OS: " + name);
    }

    public String toString() { return name; }
}