package io.github.spookylauncher.advio.security;

import io.github.spookylauncher.advio.collectors.Collector;
import io.github.spookylauncher.advio.assembly.Assembly;
import io.github.spookylauncher.advio.assembly.AssemblyIO;
import io.github.spookylauncher.advio.security.crypto.CryptographyAlgorithm;
import io.github.spookylauncher.advio.security.crypto.Cryptographers;

public final class EncryptedResourcesLoader {
    private static byte[] defaultKey;

    public static final CryptographyAlgorithm DEFAULT_ALGO = CryptographyAlgorithm.DESede;

    public static Assembly loadAssembly(Collector collector) {
        return loadAssembly(collector, DEFAULT_ALGO, defaultKey);
    }

    public static void setDefaultKey(Collector collector) {
        setDefaultKey(collector.collectBytes());
    }

    public static void setDefaultKey(byte[] defaultKey) {
        EncryptedResourcesLoader.defaultKey = defaultKey;
    }

    public static Assembly loadAssembly(Collector collector, byte[] key) {
        return loadAssembly(collector, DEFAULT_ALGO, key);
    }

    public static Assembly loadAssembly(Collector collector, CryptographyAlgorithm algorithm, byte[] key) {
        return AssemblyIO.read(Cryptographers.get(algorithm).decryptStream(collector.collectInput(), key));
    }
}