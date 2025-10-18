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

    public BufferedImage collectImage() throws IOException {
        return ImageIO.read(collectInput());
    }
    public final Reader collectReader() throws IOException {
        return new InputStreamReader(collectInput(), StandardCharsets.UTF_8);
    }
    public final Properties collectProperties() throws IOException {
        Properties props = new Properties();

        props.load(collectReader());

        return props;
    }

    public final String collectString() throws IOException {
        return IOUtils.readString(collectInput());
    }

    public InputStream collectInput() throws IOException {
        InputStream in = collectBaseInput();

        return buffering && !(in instanceof BufferedInputStream) ? new BufferedInputStream(in) : in;
    }

    protected abstract InputStream collectBaseInput() throws IOException;

    public abstract long size();

    public final byte[] collectBytes() throws IOException {
        InputStream in = collectInput();

        byte[] bytes = IOUtils.readBytes(in);

        in.close();

        return bytes;
    }

    public static Collector of(Path path) { return of(path.toFile()); }

    public static Collector of(File file) { return new FileCollector(file); }

    public static Collector of(byte[] bytes) throws IOException {
        return of(new ByteArrayInputStream(bytes));
    }

    public static Collector of(InputStream in) throws IOException { return new StreamCollector(in); }
}