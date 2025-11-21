package io.github.spookylauncher.util.structures.tuple;

public class Tuple5<X, Y, Z, W, V> extends Tuple4<X, Y, Z, W> {
    public final V v;

    public Tuple5(X x) {
        this(x, null);
    }

    public Tuple5(X x, Y y) {
        this(x, y, null);
    }

    public Tuple5(X x, Y y, Z z) {
        this(x, y, z, null);
    }

    public Tuple5(X x, Y y, Z z, W w) {
        this(x, y, z, w, null);
    }

    public Tuple5(X x, Y y, Z z, W w, V v) {
        super(x, y, z, w);
        this.v = v;
    }

    public static <X, Y, Z, W, V> Tuple5<X, Y, Z, W, V> of(X x, Y y, Z z, W w, V v) {
        return new Tuple5<>(x, y, z, w, v);
    }
}