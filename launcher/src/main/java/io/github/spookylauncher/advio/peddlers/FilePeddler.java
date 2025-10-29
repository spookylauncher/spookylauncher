package io.github.spookylauncher.advio.peddlers;

import java.io.*;

public final class FilePeddler extends Peddler {
    public FilePeddler(File file) { this(file.getAbsolutePath()); }

    public FilePeddler(String path) {
        super(path);

        if(new File(path).isDirectory()) throw new IllegalArgumentException("output path is directory");
    }

    @Override
    public void peddleStream(InputStream in) throws IOException {
        in = new BufferedInputStream(in);

        FileOutputStream out = new FileOutputStream(path);

        byte[] buffer = new byte[4096];
        int len;

        while((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        out.close();
    }
}