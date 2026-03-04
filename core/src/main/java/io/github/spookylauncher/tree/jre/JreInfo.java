package io.github.spookylauncher.tree.jre;

import io.github.spookylauncher.io.OSType;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class JreInfo {
    @SerializedName("vendor") public String vendor = "";
    @SerializedName("majorVersion") public int majorVersion;
    @SerializedName("fullVersion") public String fullVersion;
    @SerializedName("downloadUrls") public HashMap<OSType, String> downloads;

    public String toString() {
        return String.valueOf(majorVersion);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof JreInfo)) return false;

        JreInfo info = (JreInfo) obj;

        if
        (
                info.majorVersion != majorVersion
                || !fullVersion.equals(info.fullVersion)
                || !vendor.equals(info.vendor)
        ) return false;

        return true;
    }

    public static int getIndexOfNewer(JreInfo[] jres) {
        int index = -1;
        int major = -1;

        for(int i = 0;i < jres.length;i++) {
            if(jres[i].majorVersion > major) {
                major = jres[i].majorVersion;
                index = i;
            }
        }

        return index;
    }
}