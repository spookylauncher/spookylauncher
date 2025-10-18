package io.github.spookylauncher.tree;

import io.github.spookylauncher.advio.Os;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class DownloadableFile {
    @SerializedName("name") public String name;
    @SerializedName("downloadUrl") public String downloadUrl;
    @SerializedName("sha1") public String sha1;

    @SerializedName("downloadUrls") public HashMap<Os, String> downloadUrls;

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
        return getDownloadUrl(Os.CURRENT);
    }

    public String getDownloadUrl(Os os) {
        if(downloadUrls == null) return downloadUrl;

        return downloadUrls.get(os);
    }
}