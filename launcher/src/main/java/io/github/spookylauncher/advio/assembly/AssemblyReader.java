package io.github.spookylauncher.advio.assembly;

import io.github.spookylauncher.advio.collectors.Collector;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

public final class AssemblyReader {

    public Assembly read(Collector collector) throws IOException {
        return read(collector.collectInput());
    }

    public Assembly read(File file) throws IOException {
        return read(Files.newInputStream(file.toPath()));
    }

    public Assembly read(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);

        Assembly assembly = read(dataInput);

        dataInput.close();

        return assembly;
    }

    public Assembly read(DataInputStream input) throws IOException {
        AssemblyHeader header = readHeader(input);

        HashMap<String, Blob> map = new HashMap<>();

        Blob blob = null;

        int blobIndex = 0;
        long endOffset = 0;
        int positionInBlobData = 0;
        long position = 0;

        int len;

        while(true) {
            if(position == endOffset) {
                if(blob != null) map.put(blob.name, blob);

                blob = header.blobs[blobIndex];
                endOffset = header.endOffsets[blobIndex++];
                blob.data = new byte[(int) (endOffset - position)];
                positionInBlobData = 0;
                continue;
            }

            if((len = input.read()) == -1 || header.blobs.length - 1 == blobIndex) break;

            blob.data[positionInBlobData++] = (byte) len;

            position++;
        }

        return new Assembly(map);
    }

    private AssemblyHeader readHeader(DataInputStream input) throws IOException {
        for(int i = 0;i < ".ASSEMBLY".length();i++) input.readByte();

        int version = input.readInt();
        int blobsCount = input.readInt();

        Blob[] blobs = new Blob[blobsCount];
        long[] endOffsets = new long[blobsCount];

        for(int i = 0;i < blobs.length;i++) {
            blobs[i] = new Blob(input.readUTF());
            endOffsets[i] = input.readLong();
        }

        return new AssemblyHeader(version, blobs, endOffsets);
    }
}