package io.github.spookylauncher.wrapper.security;

import io.github.spookylauncher.wrapper.security.file.FilesChecker;

import java.util.*;
import java.io.File;

public final class SecurityManagerFactory {

    private Map<File, Integer> fileAccesses = new HashMap<>();
    private Map<File, Integer> directoriesAccesses = new HashMap<>();

    private final Map<String, Set<Integer>> hosts = new HashMap<>();

    private Set<String> permissions = new HashSet<>();

    private final Set<String> librariesNames = new HashSet<>();
    private final Set<File> librariesFiles = new HashSet<>();

    private final Set<File> executables = new HashSet<>();
    private CheckMode
    filesCheckerMode, permissionsCheckerMode,
    executeCheckerMode, nativeLinkCheckerMode, networkCheckerMode;

    private Set<String> ignoredPackages = new HashSet<>();

    public SecurityManagerFactory addIgnoredPackage(String ignoredPackage) {
        this.ignoredPackages.add(ignoredPackage);
        return this;
    }

    public SecurityManagerFactory addIgnoredPackages(String... ignoredPackages) {
        return addIgnoredPackages(Arrays.asList(ignoredPackages));
    }

    public SecurityManagerFactory setIgnoredPackages(String... ignoredPackages) {
        return setIgnoredPackages(Arrays.asList(ignoredPackages));
    }

    public SecurityManagerFactory addIgnoredPackages(Collection<String> ignoredPackages) {
        this.ignoredPackages.addAll(ignoredPackages);
        return this;
    }

    public SecurityManagerFactory setIgnoredPackages(Collection<String> ignoredPackages) {
        this.ignoredPackages.clear();
        return addIgnoredPackages(ignoredPackages);
    }

    private NetworkChecker getNetworkChecker() {
        if(networkChecker == null && !hosts.isEmpty()) {
            return new NetworkChecker(
                        networkCheckerMode,
                        hosts
                    );
        } else return networkChecker;
    }

    public SecurityManagerFactory addPorts(String host, int...ports) {
        for(int port : ports)
            addPorts(host, port);

        return this;
    }

    public SecurityManagerFactory addPorts(String host, Integer[] ports) {
        return addPorts(host, Arrays.asList(ports));
    }

    public SecurityManagerFactory addPorts(String host, Collection<Integer> ports) {
        for(Integer port : ports) {
            if(port != null) addPort(host, port);
        }

        return this;
    }

    public SecurityManagerFactory addPort(String host, int port) {
        Set<Integer> ports;

        if(this.hosts.containsKey(host)) ports = this.hosts.computeIfAbsent(host, k -> new HashSet<>());
        else {
            ports = new HashSet<>();
            this.hosts.put(host, ports);
        }

        ports.add(port);

        return this;
    }

    public SecurityManagerFactory acceptAllPorts(String host) {
        this.hosts.put(host, null);
        return this;
    }

    public SecurityManagerFactory addHost(String host) {
        if(!this.hosts.containsKey(host))
            this.hosts.put(host, null);

        return this;
    }

    public SecurityManagerFactory addHosts(String...hosts) {
        return this.addHosts(Arrays.asList(hosts));
    }

    public SecurityManagerFactory setHosts(String...hosts) {
        return this.setHosts(Arrays.asList(hosts));
    }

    public SecurityManagerFactory addHosts(Collection<String> hosts) {
        for(String host : hosts) {
            if(!this.hosts.containsKey(host)) this.hosts.put(host, null);
        }

        return this;
    }

    public SecurityManagerFactory setHosts(Collection<String> hosts) {
        this.hosts.clear();
        return addHosts(hosts);
    }

    public SecurityManagerFactory addHosts(Map<String, Set<Integer>> hosts) {
        this.hosts.putAll(hosts);
        return this;
    }

    public SecurityManagerFactory setHosts(Map<String, Set<Integer>> hosts) {
        this.hosts.clear();
        return addHosts(hosts);
    }

    public SecurityManagerFactory setNetworkCheckMode(CheckMode mode) {
        this.networkCheckerMode = mode;
        return this;
    }

    public SecurityManagerFactory() {
        filesCheckerMode = CheckMode.DENY_ALL_EXCEPT;
        permissionsCheckerMode = CheckMode.PERMIT_ALL_EXCEPT;
        executeCheckerMode = CheckMode.DENY_ALL_EXCEPT;
        networkCheckerMode = CheckMode.PERMIT_ALL_EXCEPT;
        nativeLinkCheckerMode = CheckMode.DENY_ALL_EXCEPT;
    }

    public SimpleSecurityManager build() {
        FilesChecker filesChecker = getFilesChecker();

        return new SimpleSecurityManager(
                getPermissionsChecker(filesChecker),
                filesChecker,
                getNativeLinkChecker(),
                getExecuteChecker(),
                getNetworkChecker(),
                ignoredPackages
        );
    }

    private FilesChecker getFilesChecker() {
        if(filesChecker == null && !(fileAccesses.isEmpty() && directoriesAccesses.isEmpty())) {
            return new FilesChecker(
                            filesCheckerMode,
                            fileAccesses,
                            directoriesAccesses
                    );
        } else return filesChecker;
    }

    private PermissionsChecker getPermissionsChecker(FilesChecker filesChecker) {
        if(permissionsChecker == null && !permissions.isEmpty()) {
            return new PermissionsChecker(
                            permissionsCheckerMode,
                            permissions,
                            filesChecker
                    );
        } else return permissionsChecker;
    }

    private NativeLinkChecker getNativeLinkChecker() {
        if(nativeLinkChecker == null && !(librariesNames.isEmpty() && librariesFiles.isEmpty()) ) {
            return new NativeLinkChecker(
                    nativeLinkCheckerMode,
                    librariesNames,
                    librariesFiles
            );
        } else return nativeLinkChecker;
    }

    private ExecuteChecker getExecuteChecker() {
        if(executeChecker == null && !executables.isEmpty()) {
            return new ExecuteChecker(
                    executeCheckerMode,
                    executables
            );
        } else return executeChecker;
    }

    public SecurityManagerFactory setExecuteCheckMode(CheckMode mode) {
        executeCheckerMode = mode;
        return this;
    }

    public SecurityManagerFactory addExecutable(File executable) {
        this.executables.add(executable);
        return this;
    }

    public SecurityManagerFactory addExecutables(File...executables) {
        return addExecutables(Arrays.asList(executables));
    }

    public SecurityManagerFactory setExecutables(File...executables) {
        return setExecutables(Arrays.asList(executables));
    }

    public SecurityManagerFactory addExecutables(Collection<File> executables) {
        this.executables.addAll(executables);
        return this;
    }

    public SecurityManagerFactory setExecutables(Collection<File> executables) {
        this.executables.clear();
        return addExecutables(executables);
    }

    public SecurityManagerFactory setNativeLinkCheckMode(CheckMode mode) {
        this.nativeLinkCheckerMode = mode;
        return this;
    }

    public SecurityManagerFactory setLibrariesFiles(File...librariesFiles) {
        return setLibrariesFiles(Arrays.asList(librariesFiles));
    }

    public SecurityManagerFactory addLibrariesFiles(File...librariesFiles) {
        return addLibrariesFiles(Arrays.asList(librariesFiles));
    }

    public SecurityManagerFactory addLibrariesFiles(Collection<File> librariesFiles) {
        this.librariesFiles.addAll(librariesFiles);
        return this;
    }

    public SecurityManagerFactory setLibrariesFiles(Collection<File> librariesFiles) {
        this.librariesFiles.clear();
        return addLibrariesFiles(librariesFiles);
    }

    public SecurityManagerFactory addLibraryFile(File libraryFile) {
        this.librariesFiles.add(libraryFile);
        return this;
    }

    public SecurityManagerFactory addLibraryName(String libraryName) {
        this.librariesNames.add(libraryName);
        return this;
    }

    public SecurityManagerFactory setLibrariesNames(String...librariesNames) {
        return setLibrariesNames(Arrays.asList(librariesNames));
    }

    public SecurityManagerFactory addLibrariesNames(String...librariesNames) {
        return addLibrariesNames(Arrays.asList(librariesNames));
    }

    public SecurityManagerFactory addLibrariesNames(Collection<String> librariesNames) {
        this.librariesNames.addAll(librariesNames);
        return this;
    }

    public SecurityManagerFactory setLibrariesNames(Collection<String> librariesNames) {
        this.librariesNames.clear();
        return addLibrariesNames(librariesNames);
    }

    public SecurityManagerFactory setPermissionsCheckMode(CheckMode mode) {
        this.permissionsCheckerMode = mode;
        return this;
    }

    public SecurityManagerFactory addPermission(String permission) {
        this.permissions.add(permission);
        return this;
    }

    public SecurityManagerFactory addPermissions(String...permissions) {
        return addPermissions(Arrays.asList(permissions));
    }

    public SecurityManagerFactory setPermissions(String...permissions) {
        return setPermissions(Arrays.asList(permissions));
    }

    public SecurityManagerFactory addPermissions(Collection<String> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    public SecurityManagerFactory setPermissions(Collection<String> permissions) {
        this.permissions.clear();
        return addPermissions(permissions);
    }

    public SecurityManagerFactory setFilesCheckMode(CheckMode mode) {
        this.filesCheckerMode = mode;
        return this;
    }

    public SecurityManagerFactory setAccessToFiles(int accessMask, File... files) {
        setAccessToFiles(accessMask, Arrays.asList(files));
        return this;
    }

    public SecurityManagerFactory setAccessToDirectories(int accessMask, File... directories) {
        setAccessToDirectories(accessMask, Arrays.asList(directories));
        return this;
    }

    public SecurityManagerFactory setAccessToFiles(int accessMask, Collection<File> files) {
        for(File file : files) setAccessToFile(accessMask, file);
        return this;
    }

    public SecurityManagerFactory setAccessToDirectories(int accessMask, Collection<File> directories) {
        for(File directory : directories) setAccessToDirectory(accessMask, directory);
        return this;
    }

    public SecurityManagerFactory setAccessToDirectory(int accessMask, File directory) {
        directoriesAccesses.put(directory, accessMask);
        return this;
    }

    public SecurityManagerFactory setAccessToFile(int accessMask, File file) {
        if(file.isDirectory()) setAccessToDirectory(accessMask, file);
        else fileAccesses.put(file, accessMask);
        return this;
    }

    private ExecuteChecker executeChecker;
    private NativeLinkChecker nativeLinkChecker;
    private NetworkChecker networkChecker;
    private PermissionsChecker permissionsChecker;
    private FilesChecker filesChecker;

    public SecurityManagerFactory setExecuteChecker(ExecuteChecker checker) {
        this.executeChecker = checker;
        return this;
    }

    public SecurityManagerFactory setNativeLinkChecker(NativeLinkChecker checker) {
        this.nativeLinkChecker = checker;
        return this;
    }

    public SecurityManagerFactory setNetworkChecker(NetworkChecker checker) {
        this.networkChecker = checker;
        return this;
    }

    public SecurityManagerFactory setPermissionsChecker(PermissionsChecker checker) {
        this.permissionsChecker = checker == null ? null : new PermissionsChecker(checker, filesChecker);
        return this;
    }

    public SecurityManagerFactory setFilesChecker(FilesChecker checker) {
        this.filesChecker = checker;
        this.setPermissionsChecker(permissionsChecker);
        return this;
    }
}