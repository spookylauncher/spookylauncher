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

        FileOutputStream fos;
        BufferedOutputStream out = new BufferedOutputStream(fos = new FileOutputStream(path));

        int len;

        while((len = in.read()) != -1) {
            out.write(len);
        }

        out.flush();
        out.close();
        fos.close();
    }
}