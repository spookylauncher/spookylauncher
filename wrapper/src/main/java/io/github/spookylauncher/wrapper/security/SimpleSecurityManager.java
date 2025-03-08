package io.github.spookylauncher.wrapper.security;

import io.github.spookylauncher.wrapper.security.file.FileAccessType;
import io.github.spookylauncher.wrapper.security.file.FilesChecker;

import java.security.Permission;
import java.util.*;

public final class SimpleSecurityManager extends SecurityManager {
    private final PermissionsChecker permsChecker;
    private final FilesChecker filesChecker;

    private final NativeLinkChecker nativeLinkChecker;

    private final ExecuteChecker executeChecker;

    private final NetworkChecker networkChecker;

    private final Set<String> ignoredPackages;

    public SimpleSecurityManager(
            PermissionsChecker permsChecker,
            FilesChecker filesChecker,
            NativeLinkChecker nativeLinkChecker,
            ExecuteChecker executeChecker,
            NetworkChecker networkChecker,
            Set<String> ignoredPackages
    ) {
        super();
        this.permsChecker = permsChecker;
        this.filesChecker = filesChecker;
        this.nativeLinkChecker = nativeLinkChecker;
        this.executeChecker = executeChecker;
        this.networkChecker = networkChecker;
        this.ignoredPackages = Collections.unmodifiableSet(ignoredPackages);
    }

    private boolean disallowActions() {
        System.out.println("test: " + getClassContext()[0]);
        return !allowActions(getClassContext()[0]);
    }

    private boolean allowActions(Class<?> clazz) {
        String name = clazz.getName();

        for(String ignoredPackage : ignoredPackages) {
            if(name.startsWith(ignoredPackage)) return true;
        }

        return false;
    }

    @Override
    public void checkPermission(Permission perm) {
        super.checkPermission(perm);
        if(permsChecker != null && disallowActions()) permsChecker.checkPermission(perm);
    }

    @Override
    public void checkRead(String file, Object context) {
        super.checkRead(file, context);
        this.checkRead(file);
    }

    @Override
    public void checkRead(String file) {
        super.checkRead(file);
        if(filesChecker != null && disallowActions()) filesChecker.checkAccess(file, FileAccessType.READ);
    }

    @Override
    public void checkWrite(String file) {
        super.checkWrite(file);
        if(filesChecker != null && disallowActions()) filesChecker.checkAccess(file, FileAccessType.WRITE);
    }

    @Override
    public void checkDelete(String file) {
        super.checkDelete(file);
        if(filesChecker != null && disallowActions()) filesChecker.checkAccess(file, FileAccessType.DELETE);
    }

    @Override
    public void checkLink(String lib) {
        super.checkLink(lib);
        if(nativeLinkChecker != null && disallowActions()) nativeLinkChecker.checkLink(lib);
    }

    @Override
    public void checkExec(String cmd) {
        super.checkExec(cmd);
        if(executeChecker != null && disallowActions()) executeChecker.checkExec(cmd);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        super.checkConnect(host, port, context);
        this.checkConnect(host, port);
    }

    @Override
    public void checkConnect(String host, int port) {
        super.checkConnect(host, port);
        if(networkChecker != null && disallowActions()) networkChecker.checkConnect(host, port);
    }
}