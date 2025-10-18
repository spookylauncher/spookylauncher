package io.github.spookylauncher.advio.security.hash;

import java.util.HashMap;

public final class HashCalculators {
    private static final HashMap<String, HashCalculator> calculators = new HashMap<>();

    public static HashCalculator get(HashingAlgorithm algorithm) {
        return get(algorithm.getAlgorithmID());
    }

    public static HashCalculator get(String algorithm) {
        if (!calculators.containsKey(algorithm)) calculators.put(algorithm, new HashCalculator(algorithm));

        return calculators.get(algorithm);
    }
}