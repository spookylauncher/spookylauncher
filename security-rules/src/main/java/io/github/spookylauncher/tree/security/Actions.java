package io.github.spookylauncher.tree.security;

public final class Actions {
    public SecurityActionType files = SecurityActionType.PERMIT;
    public SecurityActionType permissions = SecurityActionType.DENY;
    public SecurityActionType network = SecurityActionType.DENY;
    public SecurityActionType execute = SecurityActionType.PERMIT;
    public SecurityActionType nativeLink = SecurityActionType.PERMIT;
}