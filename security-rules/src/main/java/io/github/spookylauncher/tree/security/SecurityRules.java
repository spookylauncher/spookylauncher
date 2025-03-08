package io.github.spookylauncher.tree.security;

import java.util.HashMap;
import java.util.HashSet;

public final class SecurityRules {
    public HashMap<String, String> files;
    public HashMap<String, String> dirs;
    public HashSet<String> executables;
    public HashSet<String> nativeLibsNames;
    public HashSet<String> nativeLibsFiles;
    public HashSet<String> permissions;
    public HashSet<Host> hosts;

    public Actions actions = new Actions();
}