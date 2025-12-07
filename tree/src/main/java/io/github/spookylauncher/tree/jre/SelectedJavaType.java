package io.github.spookylauncher.tree.jre;

import com.google.gson.annotations.SerializedName;

public enum SelectedJavaType {
    @SerializedName("launcher") LAUNCHER,
    @SerializedName("external") EXTERNAL,
    @SerializedName("custom") CUSTOM
}