package io.github.spookylauncher.advio.collectors;

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

        while((len = stream.read()) != -1)
            baos.write(len);

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