package io.github.spookylauncher.tree.launcher;

import com.google.gson.annotations.SerializedName;

public class LauncherVersionInfo {
    public static final LauncherVersionInfo CURRENT;

    @SerializedName("maxSupportedVersionsManifestVersion") public int maxSupportedVersionsManifestVersion;
    @SerializedName("name") public String name;
    @SerializedName("stage") public DevStage stage;
    @SerializedName("versionCode") public int versionCode;
    @SerializedName("jar") public String jar;

    static {
        LauncherVersionInfo version = new LauncherVersionInfo();

        version.stage = DevStage.BETA;
        version.name = "1.0";
        version.versionCode = 0;

        CURRENT = version;
    }
}