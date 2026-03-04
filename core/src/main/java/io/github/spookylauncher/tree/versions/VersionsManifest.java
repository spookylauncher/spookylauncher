package io.github.spookylauncher.tree.versions;

import io.github.spookylauncher.io.collectors.URLCollector;
import io.github.spookylauncher.util.Json;
import com.google.gson.annotations.SerializedName;
import io.github.spookylauncher.util.StringUtils;

import java.util.HashMap;

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
            VersionInfo info = Json.collectJson(new URLCollector(repo + "/versions/" + StringUtils.urlEncode(version) + "/" + StringUtils.urlEncode(version) + ".json"), VersionInfo.class);

            cachedVersions.put(version, info);

            return info;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}