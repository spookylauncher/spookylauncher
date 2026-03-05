package io.github.spookylauncher.ui.javafx;

import java.util.concurrent.CountDownLatch;

public class JFXProxy {
    private static JFXUIProvider provider;
    private static JFXApplication app;
    private static CountDownLatch latch;

    public static void setInitLatch(final CountDownLatch latch) {
        JFXProxy.latch = latch;
    }

    public static CountDownLatch getInitLatch() {
        return latch;
    }

    public static void setProvider(final JFXUIProvider provider) {
        JFXProxy.provider = provider;
    }

    public static void setApp(final JFXApplication app) {
        JFXProxy.app = app;
    }

    public static JFXApplication getApp() {
        return JFXProxy.app;
    }

    public static JFXUIProvider getProvider() {
        return JFXProxy.provider;
    }
}
