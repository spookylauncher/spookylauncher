package io.github.spookylauncher.advio.security.hash;

public enum HashingAlgorithm {
    SHA2_256("SHA-256"),
    SHA2_512("SHA-512");

    private final String name;

    HashingAlgorithm(String name) {
        this.name = name;
    }

    public String getAlgorithmID() {
        return this.name;
    }
}