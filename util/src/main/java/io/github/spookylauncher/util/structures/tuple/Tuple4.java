package io.github.spookylauncher.util.structures.tuple;

public class Tuple4<X, Y, Z, W> extends  Tuple3<X, Y, Z> {
    public final W w;

    public Tuple4(X x) {
        this(x, null);
    }

    public Tuple4(X x, Y y) {
        this(x, y, null);
    }

    public Tuple4(X x, Y y, Z z) {
        this(x, y, z, null);
    }

    public Tuple4(X x, Y y, Z z, W w) {
        super(x, y, z);
        this.w = w;
    }

    public static <X, Y, Z, W> Tuple4<X, Y, Z, W> of(X x, Y y, Z z, W w) {
        return new Tuple4<>(x, y, z, w);
    }
}