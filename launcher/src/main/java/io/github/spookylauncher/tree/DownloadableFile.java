package io.github.spookylauncher.tree;

import io.github.spookylauncher.io.OSType;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class DownloadableFile {
    @SerializedName("name") public String name;
    @SerializedName("downloadUrl") public String downloadUrl;
    @SerializedName("sha1") public String sha1;

    @SerializedName("downloadUrls") public HashMap<OSType, String> downloadUrls;

    public DownloadableFile() {}

    public DownloadableFile(String url) {
        this(url, false);
    }

    public DownloadableFile(String url, boolean autoCalculateSha1) {
        this.downloadUrl = url;

        if(autoCalculateSha1) {
            new Thread(() -> {
                // TODO: new hash calculation
                sha1 = "";
            }).run();
        }
    }

    public DownloadableFile(String url, String sha1) {
        this.downloadUrl = url;
        this.sha1 = sha1;
    }

    public String getDownloadUrl() {
        return getDownloadUrl(OSType.CURRENT);
    }

    public String getDownloadUrl(OSType osType) {
        if(downloadUrls == null) return downloadUrl;

        return downloadUrls.get(osType);
    }
}