package io.github.spookylauncher.advio.collectors;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileCollector extends Collector {

    public FileCollector(File file) { this(file.getAbsolutePath()); }
    public FileCollector(String path) {
        super(path);

        if(new File(path).isDirectory()) throw new IllegalArgumentException("input path is directory");
    }

    @Override
    public InputStream collectBaseInput() {
        try {
            return Files.newInputStream(Paths.get(path));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long size() {
        return new File(path).length();
    }
}