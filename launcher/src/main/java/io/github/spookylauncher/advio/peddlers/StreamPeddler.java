package io.github.spookylauncher.advio.peddlers;

import java.io.*;

public class StreamPeddler extends Peddler {
    private BufferedOutputStream out;

    public StreamPeddler(OutputStream out) {
        super(null);

        this.out = new BufferedOutputStream(out);
    }

    @Override
    public void peddleStream(InputStream in) {
        try {
            int len;

            while((len = in.read()) != -1) out.write(in.read());

            out.flush();
            in.close();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}