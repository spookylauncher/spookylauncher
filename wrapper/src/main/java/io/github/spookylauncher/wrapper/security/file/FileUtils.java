package io.github.spookylauncher.wrapper.security.file;

import io.github.spookylauncher.wrapper.util.StringUtils;

import java.io.File;
import java.util.*;

public final class FileUtils {
    public static boolean isSubOf(File parent, File child) {
        return isSubOf(parent.getPath(), child.getPath());
    }

    public static String replaceBackSlashes(String path) {
        return path.contains("\\") ? path.replace("\\", "/") : path;
    }

    public static boolean isSubOf(String parent, String child) {
        parent = replaceBackSlashes(parent);
        child = replaceBackSlashes(child);

        if(!parent.endsWith("/")) parent = parent + "/";

        return child.startsWith(parent);
    }

    public static void sortByDepth(List<String> parents, String child) {
        parents.sort(Comparator.comparingInt(a -> getDepth(a, child)));
    }

    public static int getClosestParent(Collection<String> parents, String child) {
        int minDepth = Integer.MAX_VALUE;
        int closest = -1;

        int index = 0;

        for(String parent : parents) {
            int depth = getDepth(parent, child);

            if(depth < minDepth) {
                minDepth = depth;
                closest = index;
            }

            index++;
        }

        return closest;
    }

    public static int getDepth(String parent, String child) {
        parent = replaceBackSlashes(parent);
        child = replaceBackSlashes(child);

        if(!parent.endsWith("/")) parent = parent + "/";

        if(!isSubOf(parent, child)) throw new IllegalArgumentException("parent is not parent of child");

        return Math.abs(StringUtils.countOf(parent, '/') - StringUtils.countOf(child, '/'));
    }
}