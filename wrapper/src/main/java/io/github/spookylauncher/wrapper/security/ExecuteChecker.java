package io.github.spookylauncher.wrapper.security;

import java.io.File;
import java.util.*;

public final class ExecuteChecker extends Checker {
    private final Set<String> executables;

    public ExecuteChecker(Set<File> executables) {
        this(CheckMode.PERMIT_ALL_EXCEPT, executables);
    }

    public ExecuteChecker(CheckMode mode, Set<File> executables) {
        super(mode);

        Set<String> paths = new HashSet<>();

        for(File file : executables) paths.add(file.getPath());

        this.executables = Collections.unmodifiableSet(new LinkedHashSet<>(paths));
    }

    public void checkExec(String executable) {
        executable = new File(executable).getPath();

        if((mode == CheckMode.PERMIT_ALL_EXCEPT) == executables.contains(executable))
            throw new SecurityException("not permitted executable \"" + executable + "\"");
    }
}