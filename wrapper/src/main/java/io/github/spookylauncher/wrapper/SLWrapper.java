package io.github.spookylauncher.wrapper;

import io.github.spookylauncher.wrapper.security.SecurityManagerFactory;
import io.github.spookylauncher.wrapper.tasks.MinecraftClassSetup;
import io.github.spookylauncher.wrapper.tasks.MinecraftLauncher;
import io.github.spookylauncher.wrapper.tasks.SecurityManagerFactoryTuner;
import io.github.spookylauncher.wrapper.transform.TDRTransformer;
import io.github.spookylauncher.wrapper.util.proxy.ProxyClassLoader;
import io.github.spookylauncher.wrapper.util.ArgumentsParser;
import io.github.spookylauncher.wrapper.util.ClassUtils;
import io.github.spookylauncher.wrapper.util.log.Level;
import io.github.spookylauncher.wrapper.util.log.Logger;

import java.io.File;
import java.util.*;

public final class SLWrapper implements Runnable {
    public static final String VERSION = "1.0-A";
    public static final String LOG_ID = "spookylaunch wrapper " + VERSION;

    private final Properties config;

    private SLWrapper(Properties config) {
        this.config = config;
    }

    public static void main(String[] args) {
        new SLWrapper(ArgumentsParser.parseArguments(args)).run();
    }

    @Override
    public void run() {
        boolean error = false;

        try {
            MinecraftClassSetup mcSetupTask = new MinecraftClassSetup();

            if (Boolean.parseBoolean(config.getProperty("useIsolation", "false"))) {
                SecurityManagerFactory factory = new SecurityManagerFactory();

                SecurityManagerFactoryTuner tuner = new SecurityManagerFactoryTuner();

                if (!tuner.setup(config, factory)) error = true;
                else {
                    factory.addIgnoredPackage("io.github.spookylauncher.wrapper");

                    System.setSecurityManager(factory.build());
                }
            }

            if (!error) {

                ProxyClassLoader proxyClassLoader = new ProxyClassLoader(true);

                proxyClassLoader.closePackage("io.github.spookylauncher.spookylauncher");

                if (!mcSetupTask.setup(proxyClassLoader, config)) {
                    Logger.log(Level.ERROR, LOG_ID, "failed to setup minecraft");
                    error = true;
                } else {
                    File resourcesDirectory = new File(Objects.requireNonNull(config.getProperty("resourcesDirectory"), "resourcesDirectory property is not specified"));

                    String downloadResClass = config.getProperty("tdrClass", "net.minecraft.src.ThreadDownloadResources");

                    if (!ClassUtils.classExists(SLWrapper.class, downloadResClass)) {
                        Logger.log(Level.FATAL, LOG_ID, "ThreadDownloadResources class (" + downloadResClass + ") not found");
                        System.exit(1);
                        return;
                    }

                    proxyClassLoader.addSpecializedTransformer(
                            downloadResClass,
                            new TDRTransformer
                                    (
                                            config.getProperty("resourcesProxyHost", "betacraft.uk"),
                                            Integer.parseInt(config.getProperty("resourcesProxyPort", "11705")),
                                            resourcesDirectory
                                    )
                    );

                    MinecraftLauncher launcher = new MinecraftLauncher();

                    error = !launcher.launch(mcSetupTask.getMinecraftClass(), config);
                }
            }
        } catch (final Exception e) {
            Logger.log(Level.FATAL, LOG_ID, "unexpected exception was occurred: ");
            Logger.log(Level.FATAL, LOG_ID, e);
        } finally {
            if (error) {
                Logger.log(Level.FATAL, LOG_ID, "failed to launch minecraft from SL Wrapper");
                System.exit(1);
            }
        }
    }
}