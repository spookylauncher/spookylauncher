package io.github.spookylauncher.wrapper.security;

public abstract class Checker {
    protected final CheckMode mode;

    public CheckMode getMode() {
        return this.mode;
    }

    public Checker() {
        this(CheckMode.DENY_ALL_EXCEPT);
    }

    public Checker(CheckMode mode) {
        this.mode = mode;
    }
}