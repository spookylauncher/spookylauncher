package io.github.spookylauncher.wrapper.security;

import io.github.spookylauncher.wrapper.security.file.FileAccessType;
import io.github.spookylauncher.wrapper.security.file.FilesChecker;

import java.io.FilePermission;
import java.security.Permission;
import java.util.*;

public final class PermissionsChecker extends Checker {
    private final Set<String> permissions;
    private final FilesChecker filesChecker;

    PermissionsChecker(PermissionsChecker instance, FilesChecker filesChecker) {
        this(instance.mode, instance.permissions, filesChecker);
    }

    public PermissionsChecker(Set<String> permissions, FilesChecker filesChecker) {
        this(CheckMode.PERMIT_ALL_EXCEPT, permissions, filesChecker);
    }

    public PermissionsChecker(CheckMode mode, Set<String> permissions, FilesChecker filesChecker) {
        super(mode);
        this.permissions = Collections.unmodifiableSet(new LinkedHashSet<>(permissions));
        this.filesChecker = filesChecker;
    }

    public void checkPermission(Permission permission) {
        String name = permission.getName();

        if(
                (mode == CheckMode.PERMIT_ALL_EXCEPT) == permissions.contains(name) ||
                "setSecurityManager".equals(name)
        ) throw new SecurityException("not permitted permission \"" + name + "\"");

        if(permission instanceof FilePermission && filesChecker != null) {
            FilePermission filePermission = (FilePermission) permission;

            filesChecker.checkAccess(
                    filePermission.getName(),
                    FileAccessType.getMaskFromFilePermissionActions(filePermission.getActions())
            );
        }
    }
}