package io.github.spookylauncher.util.structures.tuple;

public class Tuple2<X, Y> {
    public final X x;
    public final Y y;

    public Tuple2(X x) {
        this(x, null);
    }

    public Tuple2(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public static <X, Y> Tuple2<X, Y> of(X x, Y y) {
        return new Tuple2<>(x, y);
    }
}