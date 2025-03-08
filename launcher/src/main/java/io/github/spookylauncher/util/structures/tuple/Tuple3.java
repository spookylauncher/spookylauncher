package io.github.spookylauncher.util.structures.tuple;

public class Tuple3<X, Y, Z> extends Tuple2<X, Y> {
    public final Z z;

    public Tuple3(X x) {
        this(x, null);
    }

    public Tuple3(X x, Y y) {
        this(x, y, null);
    }

    public Tuple3(X x, Y y, Z z) {
        super(x, y);
        this.z = z;
    }

    public static <X, Y, Z> Tuple3<X, Y, Z> of(X x, Y y, Z z) {
        return new Tuple3<>(x, y, z);
    }
}