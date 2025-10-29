package io.github.spookylauncher.advio.peddlers;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public abstract class Peddler {
    private static final CharsetEncoder UTF_ENCODER = StandardCharsets.UTF_8.newEncoder();
    protected final String path;

    public Peddler(String path) {
        this.path = path;
    }

    public final void peddleImage(RenderedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        peddleBytes(baos.toByteArray());
    }

    public final void peddleProperties(Properties props) throws IOException {
        peddleProperties(props, null);
    }

    public final void peddleProperties(Properties props, String comments) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        props.store(writer, comments);

        writer.close();
        peddleBytes(out.toByteArray());
    }

    public final void peddleString(String str) throws IOException {
        ByteBuffer buf = UTF_ENCODER.encode(CharBuffer.wrap(str));

        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);

        peddleBytes(bytes);
    }

    public abstract void peddleStream(InputStream in) throws IOException;

    public final void peddleBytes(byte[] bytes) throws IOException {
        peddleStream(new ByteArrayInputStream(bytes));
    }

    public OutputStream asStream() {
        return asStream(4096);
    }

    public OutputStream asStream(final int bufferSize) {
        return new OutputStream() {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int count = 0;

            @Override
            public void write(int b) throws IOException {
                baos.write(b);

                if(count++ == bufferSize) {
                    flush();
                }
            }

            @Override
            public void flush() throws IOException {
                peddleBytes(baos.toByteArray());
                baos.reset();
            }

            @Override
            public void close() throws IOException {
                flush();
            }
        };
    }

    public static Peddler of(Path path) { return of(path.toFile()); }

    public static Peddler of(File file) { return new FilePeddler(file); }

    public static Peddler of(OutputStream out) {
        return new StreamPeddler(out);
    }
}