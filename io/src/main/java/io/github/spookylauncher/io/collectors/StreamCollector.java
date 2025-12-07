package io.github.spookylauncher.io.collectors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamCollector extends Collector {
    private final byte[] bytes;

    public StreamCollector(InputStream stream) throws IOException {
        super(null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int len;

        byte[] buf = new byte[4096];

        while((len = stream.read(buf)) != -1)
            baos.write(buf, 0, len);

        stream.close();

        bytes = baos.toByteArray();
    }

    @Override
    public InputStream collectBaseInput() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long size() {
        return bytes.length;
    }
}