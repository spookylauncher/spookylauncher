package io.github.spookylauncher.tree;

import com.google.gson.annotations.SerializedName;

public class LibrariesCollection {
    @SerializedName("name") public String name;
    @SerializedName("libraries") public LibraryInfo[] libraries = { };

    @SerializedName("collections") public String[] collections = { };
}