package io.github.spookylauncher.ui.javafx;

class JFXProxy {
    private static JFXUIProvider provider;
    private static JFXApplication app;

    static void setProvider(final JFXUIProvider provider) {
        JFXProxy.provider = provider;
    }

    static void setApp(final JFXApplication app) {
        JFXProxy.app = app;
    }


}
