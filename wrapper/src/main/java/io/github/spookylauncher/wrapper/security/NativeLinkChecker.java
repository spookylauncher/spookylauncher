package io.github.spookylauncher.wrapper.security;

import java.io.File;
import java.util.*;

public final class NativeLinkChecker extends Checker {

    private final Set<String> librariesNames;
    private final Set<File> librariesFiles;

    public NativeLinkChecker(Set<String> librariesNames, Set<File> librariesFiles) {
        this(CheckMode.PERMIT_ALL_EXCEPT, librariesNames, librariesFiles);
    }

    public NativeLinkChecker(CheckMode mode, Set<String> librariesNames, Set<File> librariesFiles) {
        super(mode);
        this.librariesNames = Collections.unmodifiableSet(new LinkedHashSet<>(librariesNames));
        this.librariesFiles = Collections.unmodifiableSet(new LinkedHashSet<>(librariesFiles));
    }

    public void checkLink(String libName) {
        File libraryFile = new File(libName);

        if((mode == CheckMode.PERMIT_ALL_EXCEPT) == (
            libraryFile.exists() ?
            librariesFiles.contains(libraryFile) :
            librariesNames.contains(libName)
        )) throw new SecurityException("not permitted library \"" + libName + "\"");
    }
}