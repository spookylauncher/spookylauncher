package io.github.spookylauncher.tree.launcher;

import com.google.gson.annotations.SerializedName;

public enum DevStage {
    @SerializedName("snapshot")
    SNAPSHOT,
    @SerializedName("beta")
    BETA,
    @SerializedName("release")
    RELEASE
}