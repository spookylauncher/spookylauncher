package io.github.spookylauncher.advio.security.hash;

public enum HashingAlgorithm {
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256");

    private final String name;

    HashingAlgorithm(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}