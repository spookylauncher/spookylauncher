package io.github.spookylauncher.advio;

public final class AsyncOperation {
    private final Runnable operation;
    private final int priority;
    private boolean running;

    public AsyncOperation(Runnable operation) {
        this(operation, Thread.NORM_PRIORITY);
    }

    public AsyncOperation(Runnable operation, int priority) {
        this.operation = operation;
        this.priority = priority;
    }

    public void run() {
        if(running) throw new IllegalStateException("Already run"); else running = true;

        Thread thread = new Thread(operation);

        thread.setPriority(priority);

        thread.start();
    }

    public static void run(Runnable operation) {
        run(operation, Thread.NORM_PRIORITY);
    }

    public static void run(Runnable operation, int priority) {
        new AsyncOperation(operation, priority).run();
    }
}