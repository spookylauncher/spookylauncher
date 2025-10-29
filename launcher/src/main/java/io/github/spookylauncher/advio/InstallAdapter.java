package io.github.spookylauncher.advio;

import java.util.function.*;

public final class InstallAdapter {
    public Consumer<Integer> progressConsumer;
    public Consumer<String> titleConsumer;
    public Consumer<Integer> progressBarMaxConsumer;
    public Supplier<Boolean> cancelSupplier;
    public Runnable onCancel;
    public Runnable onEnd;

    public boolean consumeFullPaths = true;
}