package io.github.spookylauncher.advio.assembly;

import java.util.Collection;

public final class AssemblyHeader {

    public final int version;
    public final Blob[] blobs;

    public final long[] endOffsets;

    public AssemblyHeader(int version, Collection<Blob> blobs, long[] endOffsets) {
        this(version, blobs.toArray(new Blob[0]), endOffsets);
    }
    public AssemblyHeader(int version, Blob[] blobs, long[] endOffsets) {
        this.version = version;
        this.blobs = blobs;
        this.endOffsets = endOffsets;
    }
}