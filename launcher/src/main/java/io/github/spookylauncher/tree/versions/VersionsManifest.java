package io.github.spookylauncher.tree.versions;

import io.github.spookylauncher.advio.collectors.URLCollector;
import io.github.spookylauncher.tree.LibraryInfo;
import io.github.spookylauncher.util.io.Json;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VersionsManifest {
    @SerializedName("manifestVersion") public int manifestVersion = 0;
    private final transient HashMap<String, VersionInfo> cachedVersions = new HashMap<>();

    @SerializedName("versions") private String[] versions;

    public String[] getVersionsList() {
        return versions;
    }

    public VersionInfo getVersionInfo(final String repo, final String version) {
        if(cachedVersions.containsKey(version)) return cachedVersions.get(version);

        try {
            VersionInfo info = Json.collectJson(new URLCollector(repo + "/versions/" + version + "/" + version + ".json"), VersionInfo.class);

            cachedVersions.put(version, info);

            return info;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}