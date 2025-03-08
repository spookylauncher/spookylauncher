package io.github.spookylauncher.util.github.tree;

import com.google.gson.annotations.SerializedName;

public final class Child {
    @SerializedName("path") public String path;
    @SerializedName("mode") public String mode;
    @SerializedName("type") public String type;
    @SerializedName("sha") public String sha;
    @SerializedName("url") public String url;
    @SerializedName("size") public int size;
}