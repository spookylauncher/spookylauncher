package io.github.spookylauncher.tree.launcher;

import com.google.gson.annotations.SerializedName;

public class LauncherManifest {
    @SerializedName("versions") public LauncherVersionInfo[] versions;
}
