package io.github.spookylauncher.wrapper.tasks;

import io.github.spookylauncher.tree.security.SecurityActionType;
import io.github.spookylauncher.tree.security.SecurityRules;
import io.github.spookylauncher.tree.security.Host;
import io.github.spookylauncher.wrapper.SLWrapper;
import io.github.spookylauncher.wrapper.security.CheckMode;
import io.github.spookylauncher.wrapper.security.SecurityManagerFactory;
import io.github.spookylauncher.wrapper.security.file.FileAccessType;
import io.github.spookylauncher.wrapper.util.Json;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

import static io.github.spookylauncher.wrapper.util.log.Level.*;
import static io.github.spookylauncher.wrapper.util.log.Logger.log;

public final class SecurityManagerFactoryTuner {
    public static final String LOG_ID = SLWrapper.LOG_ID + "/security manager factory tuner";

    public boolean setup(Properties config, SecurityManagerFactory factory) {
        if(config.containsKey("securityRules")) {
            SecurityRules rules = Json.fromJson(config.getProperty("securityRules"), SecurityRules.class);

            return setup(config, rules, factory);
        } else return false;
    }

    public boolean setup(Properties config, SecurityRules rules, SecurityManagerFactory factory) {
        try {
            factory.setFilesCheckMode(actionToCheckMode(rules.actions.files));
            setAccesses(config, rules.files, factory::setAccessToFile);
            setAccesses(config, rules.dirs, factory::setAccessToDirectory);

            factory.setPermissionsCheckMode(actionToCheckMode(rules.actions.permissions));
            factory.setPermissions(rules.permissions);

            factory.setExecuteCheckMode(actionToCheckMode(rules.actions.execute));

            for(String executable : rules.executables)
                factory.addExecutable(new File(pasteProperties(config, executable)));

            factory.setNativeLinkCheckMode(actionToCheckMode(rules.actions.nativeLink));
            factory.setLibrariesNames(rules.nativeLibsNames);

            for(String file : rules.nativeLibsFiles)
                factory.addLibraryFile(new File(pasteProperties(config, file)));

            factory.setNetworkCheckMode(actionToCheckMode(rules.actions.network));

            for(Host host : rules.hosts) {
                HashSet<Integer> ports = host.getPorts();

                if(ports.isEmpty()) factory.acceptAllPorts(host.name);
                else factory.addPorts(host.name, ports);
            }

            return true;
        } catch(Exception e) {
            log(FATAL, LOG_ID, "failed to tune factory: " + e);
            return false;
        }
    }

    private CheckMode actionToCheckMode(SecurityActionType action) {
        return action == null ? null : (action == SecurityActionType.PERMIT ? CheckMode.DENY_ALL_EXCEPT : CheckMode.PERMIT_ALL_EXCEPT);
    }

    private void setAccesses(Properties config, HashMap<String, String> pathsAndMasks, BiConsumer<Integer, File> setAccessConsumer) {
        for(Map.Entry<String, String> file : pathsAndMasks.entrySet()) {
            setAccessConsumer.accept(
                    FileAccessType.getMaskFromFilePermissionActions(file.getValue()),
                    new File(pasteProperties(config, file.getKey()))
            );
        }
    }

    private String pasteProperties(Properties config, String value) {
        for(String key : config.stringPropertyNames())
            value = value.replace("${" + key + "}", config.getProperty(key));

        return value;
    }
}