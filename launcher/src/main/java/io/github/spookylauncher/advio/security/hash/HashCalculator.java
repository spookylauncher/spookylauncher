package io.github.spookylauncher.advio.security.hash;

import io.github.spookylauncher.advio.collectors.Collector;

import java.security.MessageDigest;
import java.util.Formatter;

public final class HashCalculator {
    public final String algorithm;

    public HashCalculator(HashingAlgorithm algorithm) {
        this(algorithm.getAlgorithmID());
    }

    public HashCalculator(String algorithm) {
        this.algorithm = algorithm;
    }

    public String calculateToHex(byte[] bytes) {
        return calculateToHex(Collector.of(bytes));
    }

    public byte[] calculate(byte[] bytes) {
        return calculate(Collector.of(bytes));
    }

    public String calculateToHex(Collector collector) {
        byte[] hash = calculate(collector);

        if(hash == null) return null;

        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public byte[] calculate(Collector collector) {
        try {
            return MessageDigest.getInstance(algorithm).digest(collector.collectBytes());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}