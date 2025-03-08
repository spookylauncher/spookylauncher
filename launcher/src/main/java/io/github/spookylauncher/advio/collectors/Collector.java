package io.github.spookylauncher.advio.collectors;

import io.github.spookylauncher.advio.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public abstract class Collector {
    protected final String path;
    private boolean buffering;

    public Collector(String path) {
        this.path = path;
    }

    public boolean isBuffering() {
        return this.buffering;
    }

    public void setBuffering(boolean buffering) {
        this.buffering = buffering;
    }

    public BufferedImage collectImage() {
        try {
            return ImageIO.read(collectInput());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public final Reader collectReader() {
        try {
            return new InputStreamReader(collectInput(), StandardCharsets.UTF_8);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public final Properties collectProperties() {
        try {
            Properties props = new Properties();

            props.load(collectReader());

            return props;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public final String collectString() {
        return IOUtils.readString(collectInput());
    }
    public InputStream collectInput() {
        InputStream in = collectBaseInput();

        return buffering && !(in instanceof BufferedInputStream) ? new BufferedInputStream(in) : in;
    }
    protected abstract InputStream collectBaseInput();

    public abstract long size();

    public final byte[] collectBytes() {
        try {
            InputStream in = collectInput();

            byte[] bytes = IOUtils.readBytes(in);

            in.close();

            return bytes;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Collector of(Path path) { return of(path.toFile()); }

    public static Collector of(File file) { return new FileCollector(file); }
    public static Collector of(byte[] bytes) {
        return of(new ByteArrayInputStream(bytes));
    }

    public static Collector of(InputStream in) { return new StreamCollector(in); }
}