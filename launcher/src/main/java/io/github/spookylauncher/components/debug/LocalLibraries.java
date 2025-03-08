package io.github.spookylauncher.components.debug;

import io.github.spookylauncher.tree.DownloadableFile;
import io.github.spookylauncher.tree.LibraryInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public final class LocalLibraries {
    public static final List<LibraryInfo> LOCAL_LIBS = new ArrayList<>();

    public static LibraryInfo addLocalLibrary(
            String name,
            String packet,
            String version,
            boolean isNative,
            File jar
    ) {
        final LibraryInfo libInfo = new LibraryInfo();

        libInfo.name = name;
        libInfo.packet = packet;
        libInfo.version = version;
        libInfo.isNative = isNative;

        final DownloadableFile file = new DownloadableFile();

        try {
            file.downloadUrl = jar.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        libInfo.jar = file;

        LOCAL_LIBS.add(libInfo);

        return libInfo;
    }

    static {
        addLocalLibrary(
                "sl-wrapper",
                "io.github.spookylauncher.wrapper",
                "1.0-A",
                false,
                new File("wrapper/target/wrapper-1.0-A.jar")
        );
    }
}