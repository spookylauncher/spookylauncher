package io.github.spookylauncher.ui.javafx;

public class JFXProxy {
    private static JFXUIProvider provider;
    private static JFXApplication app;

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
