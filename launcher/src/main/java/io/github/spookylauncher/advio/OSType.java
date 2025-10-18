package io.github.spookylauncher.advio;

public enum OSType {
    WINDOWS("Windows", "win"),
    LINUX("Linux", "linux", "unix", "solaris"),
    MACOS("MacOS","mac");

    public final String[] patterns;
    public final String name;

    public static final OSType CURRENT = getOsByName(System.getProperty("os.name"));

    OSType(String name, String... patterns) {
        this.name = name;
        this.patterns = patterns;
    }

    public static OSType getOsByName(String name) {
        name = name.toLowerCase();

        for(OSType osType : OSType.values()) {
            for(String pattern : osType.patterns) {
                if(name.contains(pattern)) return osType;
            }
        }

        throw new RuntimeException("Unknown OS: " + name);
    }

    public String toString() { return name; }
}