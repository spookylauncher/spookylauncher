package io.github.spookylauncher.advio.peddlers;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public abstract class Peddler {
    protected final String path;

    public Peddler(String path) {
        this.path = path;
    }

    public final void peddleImage(RenderedImage img) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            peddleBytes(baos.toByteArray());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final void peddleProperties(Properties props) {
        peddleProperties(props, null);
    }

    public final void peddleProperties(Properties props, String comments) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

            props.store(writer, comments);

            writer.close();
            peddleBytes(out.toByteArray());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public final void peddleString(String str) {
        peddleBytes(str.getBytes(StandardCharsets.UTF_8));
    }
    public abstract void peddleStream(InputStream in);

    public final void peddleBytes(byte[] bytes) {
        peddleStream(new ByteArrayInputStream(bytes));
    }
    public OutputStream asStream() {
        return asStream(8192);
    }
    public OutputStream asStream(final int bufferSize) {
        return new OutputStream() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int count = 0;

            @Override
            public void write(int b) {
                baos.write(b);

                if(count++ == bufferSize) {
                    flush();
                }
            }

            @Override
            public void flush() {
                peddleBytes(baos.toByteArray());
                baos.reset();
            }

            @Override
            public void close() {
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