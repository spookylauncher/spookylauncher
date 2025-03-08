package io.github.spookylauncher.advio.security.crypto;

import java.util.HashMap;

public final class Cryptographers {
    private static final HashMap<String, Cryptographer> cryptographers = new HashMap<>();

    public static Cryptographer get(CryptographyAlgorithm algorithm) {
        return get(algorithm.toString());
    }

    public static Cryptographer get(String algorithm) {
        if(!cryptographers.containsKey(algorithm)) cryptographers.put(algorithm, new Cryptographer(algorithm));

        return cryptographers.get(algorithm);
    }
}