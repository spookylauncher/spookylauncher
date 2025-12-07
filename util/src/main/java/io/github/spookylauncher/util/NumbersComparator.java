package io.github.spookylauncher.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public final class NumbersComparator {
    private static final Map<String, BiPredicate<Double, Double>> comparators = new HashMap<>();

    static {
        comparators.put(">", (a, b) -> a > b);
        comparators.put("<", (a, b) -> a < b);
        comparators.put(">=", (a, b) -> a >= b);
        comparators.put("<=", (a, b) -> a <= b);

        comparators.put("==", Objects::equals);
    }

    public static BiPredicate<Double, Double> getComparator(String compareType) {
        return comparators.get(compareType);
    }

    public static boolean compare(String compareType, double a, double b) {
        return
        Objects.requireNonNull(
                comparators.get(compareType), "undefined compare type: " + compareType
        ).test(a, b);
    }
}