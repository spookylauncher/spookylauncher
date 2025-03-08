package io.github.spookylauncher.advio.assembly;

public final class Blob {
    public String name;
    public byte[] data;

    public Blob(String name) {
        this(name, null);
    }

    public Blob(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }
}