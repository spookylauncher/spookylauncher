package io.github.spookylauncher.io.peddlers;

import java.io.*;

public class StreamPeddler extends Peddler {
    private final OutputStream out;

    public StreamPeddler(OutputStream out) {
        super(null);

        this.out = out;
    }

    @Override
    public void peddleStream(InputStream in) throws IOException {
        byte[] buffer = new byte[4096];

        int len;

        while((len = in.read(buffer)) != -1)
            out.write(buffer, 0, len);

        out.flush();
        in.close();
    }
}