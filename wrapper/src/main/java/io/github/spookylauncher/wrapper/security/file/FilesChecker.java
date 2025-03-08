package io.github.spookylauncher.wrapper.security.file;

import io.github.spookylauncher.wrapper.security.CheckMode;
import io.github.spookylauncher.wrapper.security.Checker;

import java.io.File;
import java.util.*;

public final class FilesChecker extends Checker {
    private final Map<String, Integer> files;
    private final Map<String, Integer> directories;

    public FilesChecker(Map<File, Integer> files, Map<File, Integer> directories) {
        this(CheckMode.DENY_ALL_EXCEPT, files, directories);
    }

    public FilesChecker(CheckMode mode, Map<File, Integer> files, Map<File, Integer> directories) {
        super(mode);
        this.files = Collections.unmodifiableMap(new LinkedHashMap<>(toPaths(files)));
        this.directories = Collections.unmodifiableMap(new LinkedHashMap<>(toPaths(directories)));
    }

    private Map<String, Integer> toPaths(Map<File, Integer> map) {
        Map<String, Integer> paths = new HashMap<>();

        for(Map.Entry<File, Integer> entry : map.entrySet())
            paths.put(entry.getKey().getPath(), entry.getValue());

        return paths;
    }

    public void checkAccess(String file, int accessType) {
        if((mode == CheckMode.DENY_ALL_EXCEPT) == (!files.containsKey(file) || (files.get(file) & accessType) == 0)) {
            List<String> parents = new ArrayList<>();

            for(String dir : directories.keySet()) {
                if(FileUtils.isSubOf(dir, file)) parents.add(dir);
            }

            if(parents.size() > 0) {
                FileUtils.sortByDepth(parents, file);

                for(String parent : parents) {
                    if((mode == CheckMode.DENY_ALL_EXCEPT) == ( (directories.get(parent) & accessType) != 0 )) return;
                }
            }

            throw new SecurityException("\"" + FileAccessType.getName(accessType) + "\" access type is not permitted for file \"" + file + "\"");
        }
    }
}