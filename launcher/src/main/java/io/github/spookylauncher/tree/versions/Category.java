package io.github.spookylauncher.tree.versions;

import com.google.gson.annotations.SerializedName;

public enum Category {
    @SerializedName("beta")
    BETA,
    @SerializedName("alpha")
    ALPHA,
    @SerializedName("release")
    RELEASE,
    @SerializedName("multiplayer")
    MULTIPLAYER,
    @SerializedName("error")
    ERROR,
    @SerializedName("chinese")
    CHINESE
}