package io.github.spookylauncher.advio;


import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public final class Environment {

    public static final long MAX_PHYSICAL_MEMORY = getPhysicalMemory();

    public static int getVersion() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }

    public static String getLocation(Class<?> clazz) {
        try {
            String location = clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

            if(location != null && location.startsWith("/")) location = location.substring(1);

            return location;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long getPhysicalMemory() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }
}