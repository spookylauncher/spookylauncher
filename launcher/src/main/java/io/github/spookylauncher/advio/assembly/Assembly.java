package io.github.spookylauncher.advio.assembly;

import java.util.*;

public final class Assembly {
    private final HashMap<String, Blob> blobsMap = new HashMap<>();
    private final HashSet<Blob> blobsSet = new HashSet<>();

    public Blob getBlob(String name) {
        return blobsMap.get(name);
    }

    public byte[] getBlobData(String name) {
        return blobsMap.get(name).data;
    }

    public boolean containsBlob(String name) {
        return blobsMap.containsKey(name);
    }

    public void putBlob(String name, byte[] data) {
        putBlob(new Blob(name, data));
    }
    public void putBlob(Blob blob) {
        blobsMap.put(blob.name, blob);
        blobsSet.add(blob);
    }

    public void removeBlob(String name) {
        blobsMap.remove(name);
        blobsSet.removeIf((x) -> name.equals(x.name));
    }

    public Set<Blob> getBlobs() {
        return Collections.unmodifiableSet(blobsSet);
    }

    public Assembly(HashMap<String, Blob> blobs) {
        for(Blob blob : blobs.values()) putBlob(blob);
    }

    public Assembly(Collection<Blob> blobs) {
        for(Blob blob : blobs) putBlob(blob);
    }
}