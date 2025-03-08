package io.github.spookylauncher.tree.security;

import com.google.gson.annotations.SerializedName;

public enum SecurityActionType {
    @SerializedName("deny")
    DENY,
    @SerializedName("permit")
    PERMIT
}