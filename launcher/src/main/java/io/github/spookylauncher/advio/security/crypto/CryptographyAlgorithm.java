package io.github.spookylauncher.advio.security.crypto;

public enum CryptographyAlgorithm {
    AES("AES"),
    Blowfish("Blowfish"),
    ChaChaPoly("ChaCha20-Poly1305"),
    DES("DES"),
    DESede("DESede"),
    DiffieHellman("DiffieHellman"),
    DSA("DSA"),
    EC("EC"),
    OAEP("OAEP"),
    PBE("PBE"),
    RC2("RC2"),
    RSASSA_PSS("RSASSA-PSS"),
    XDH("XDH"),
    X25519("X25519"),
    X448("X448")
    ;

    private final String name;

    CryptographyAlgorithm(String name) {
        this.name = name;
    }

    public String getAlgorithmName() {
        return name;
    }

    public String toString() {
        return this.name;
    }
}