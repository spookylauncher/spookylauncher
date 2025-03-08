package io.github.spookylauncher.tree.mirrors;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class MirrorsManifest {
    @SerializedName("mirrors") public List<MirrorInfo> mirrors;
}