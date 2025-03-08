package io.github.spookylauncher.advio.collectors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamCollector extends Collector {
    private final byte[] bytes;

    public StreamCollector(InputStream stream) {
        super(null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int len;

        try {
            while((len = stream.read()) != -1) baos.write(len);
            stream.close();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

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