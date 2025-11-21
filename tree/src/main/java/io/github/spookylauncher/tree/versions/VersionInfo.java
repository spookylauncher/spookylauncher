package io.github.spookylauncher.tree.versions;

import io.github.spookylauncher.io.OSType;
import io.github.spookylauncher.log.Logger;
import io.github.spookylauncher.tree.DownloadableFile;
import io.github.spookylauncher.util.GeneralDate;
import io.github.spookylauncher.tree.LibraryInfo;
import io.github.spookylauncher.tree.security.SecurityRules;
import io.github.spookylauncher.util.github.GitHubAPI;
import io.github.spookylauncher.util.github.tree.Tree;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.*;

import static io.github.spookylauncher.log.Level.ERROR;

public final class VersionInfo {
    private static final String ID = "versions info parser";

    @SerializedName("name") public String name;
    @SerializedName("developer") public String developer = "?";
    @SerializedName("download") public DownloadableFile download;
    @SerializedName("javaMajorVersion") public int javaMajorVersion = 8;
    @SerializedName("singleJar") public boolean singleJar;
    @SerializedName("releaseDate") public GeneralDate releaseDate;

    @SerializedName("categories") public Category[] categories = new Category[0];

    @SerializedName("supportedPlatforms") public List<OSType> supportedPlatforms = Arrays.asList(OSType.values());

    @SerializedName("launchData") public HashMap<String, Object> launchData = new HashMap<>();
    @SerializedName("articles") public HashMap<String, String> articles = new HashMap<>();
    @SerializedName("libsCollections") public String[] libsCollections = { "BetaKit" };
    @SerializedName("libraries") public List<LibraryInfo> libraries;
    @SerializedName("previewsCount") private int previewsCount = -1;
    @SerializedName("labels") public String[] labels;
    @SerializedName("securityRules") public SecurityRules securityRules;

    public String toString() {
        return name;
    }

    public <T> T getLaunchProperty(String key) {
        return getLaunchProperty(key, null);
    }

    public <T> T getLaunchProperty(String key, T def) {
        if(launchData.containsKey(key)) return (T) launchData.get(key);
        else return def;
    }

    public boolean hasProperty(final String key) {
        return launchData.containsKey(key);
    }

    public int getPreviewsCount() {
        if(previewsCount == -1) {
            Tree previewsTree = null;

            try {
                previewsTree = GitHubAPI.getTreeFromBranch("spookylauncher", "Spooky-Launcher", "launcher/" + name + "/previews");
            } catch (IOException e) {
                Logger.log(ERROR, ID, "failed to get previews count");
                Logger.log(ERROR, ID, e);
            }

            if(previewsTree == null) previewsCount = 0;
            else previewsCount = previewsTree.tree.length;
        }

        return previewsCount;
    }
}