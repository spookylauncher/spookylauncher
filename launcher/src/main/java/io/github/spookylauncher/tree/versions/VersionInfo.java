package io.github.spookylauncher.tree.versions;

import io.github.spookylauncher.advio.Os;
import io.github.spookylauncher.tree.DownloadableFile;
import io.github.spookylauncher.tree.GeneralDate;
import io.github.spookylauncher.tree.LibraryInfo;
import io.github.spookylauncher.tree.security.SecurityRules;
import io.github.spookylauncher.util.github.GitHubAPI;
import io.github.spookylauncher.util.github.tree.Tree;
import com.google.gson.annotations.SerializedName;

import java.util.*;

public final class VersionInfo {
    @SerializedName("name") public String name;
    @SerializedName("developer") public String developer = "?";
    @SerializedName("download") public DownloadableFile download;
    @SerializedName("javaMajorVersion") public int javaMajorVersion = 8;
    @SerializedName("singleJar") public boolean singleJar;
    @SerializedName("releaseDate") public GeneralDate releaseDate;

    @SerializedName("categories") public Category[] categories = new Category[0];

    @SerializedName("supportedPlatforms") public List<Os> supportedPlatforms = Arrays.asList(Os.values());

    @SerializedName("launchData") public HashMap<String, Object> launchData = new HashMap<>();
    @SerializedName("articles") public HashMap<String, String> articles = new HashMap<>();
    @SerializedName("libsCollections") public String[] libsCollections = { "BetaKit" };
    @SerializedName("libraries") public LibraryInfo[] libraries;
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
            Tree previewsTree = GitHubAPI.getTreeFromBranch("spookylauncher", "Spooky-Launcher", "launcher/" + name + "/previews");

            if(previewsTree == null) previewsCount = 0;
            else previewsCount = previewsTree.tree.length;
        }

        return previewsCount;
    }
}