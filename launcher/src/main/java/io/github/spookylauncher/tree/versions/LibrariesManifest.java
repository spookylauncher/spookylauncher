package io.github.spookylauncher.tree.versions;

import com.google.gson.annotations.SerializedName;
import io.github.spookylauncher.tree.LibrariesCollection;
import io.github.spookylauncher.tree.LibraryInfo;

public class LibrariesManifest {
    @SerializedName("manifestVersion") public int manifestVersion = 0;
    @SerializedName("libsCollections") public LibrariesCollection[] libsCollections;
    @SerializedName("libraries") public LibraryInfo[] libraries;

    public LibraryInfo getLibrary(String name, String version) {
        for(LibraryInfo lib : libraries) {
            if(name.equals(lib.name) && version.equals(lib.version)) return lib;
        }

        return null;
    }

    public LibrariesCollection getLibsCollection(String name) {
        for(LibrariesCollection libs : libsCollections) {
            if(name.equals(libs.name)) return libs;
        }

        return null;
    }
}