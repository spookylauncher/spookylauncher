package io.github.spookylauncher.util.github.tree;

import com.google.gson.annotations.SerializedName;

public final class Tree {
    @SerializedName("sha") public String sha;
    @SerializedName("url") public String url;
    @SerializedName("tree") public Child[] tree;
    @SerializedName("truncated") public boolean truncated;
}