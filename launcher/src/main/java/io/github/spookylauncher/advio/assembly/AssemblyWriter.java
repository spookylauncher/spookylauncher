package io.github.spookylauncher.advio.assembly;

import io.github.spookylauncher.advio.peddlers.Peddler;

import java.io.*;
import java.nio.file.Files;
import java.util.Set;

public class AssemblyWriter {
    public void write(Assembly assembly, Peddler peddler) throws IOException {
        write(assembly, peddler.asStream());
    }

    public void write(Assembly assembly, File file) throws IOException {
        write(assembly, Files.newOutputStream(file.toPath()));
    }

    public void write(Assembly assembly, OutputStream output) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(output);

        write(assembly, dataOutput);

        dataOutput.close();
    }

    public void write(Assembly assembly, DataOutputStream output) throws IOException {
        Set<Blob> blobs = assembly.getBlobs();

        writeHeader(createHeader(blobs), output);

        for(Blob blob : blobs) output.write(blob.data);
    }

    protected AssemblyHeader createHeader(Set<Blob> blobs) {
        int version = 0;

        long[] endOffsets = new long[blobs.size()];
        long previousOffset = 0;

        int i = 0;

        for(Blob blob : blobs) {
            endOffsets[i] = previousOffset + blob.data.length;
            previousOffset = endOffsets[i++];
        }

        return new AssemblyHeader(version, blobs, endOffsets);
    }

    protected void writeHeader(AssemblyHeader header, DataOutputStream output) throws IOException {
        output.writeBytes(".ASSEMBLY");
        output.writeInt(header.version);

        output.writeInt(header.blobs.length);

        for(int i = 0;i < header.blobs.length;i++) {
            output.writeUTF(header.blobs[i].name);
            output.writeLong(header.endOffsets[i]);
        }
    }
}