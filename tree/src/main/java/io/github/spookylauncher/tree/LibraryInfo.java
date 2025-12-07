package io.github.spookylauncher.tree;

import com.google.gson.annotations.SerializedName;

public class LibraryInfo {
    @SerializedName("name") public String name;
    @SerializedName("packet") public String packet;
    @SerializedName("version") public String version;
    @SerializedName("isNative") public boolean isNative;
    @SerializedName("jar") public DownloadableFile jar;
}