package io.github.spookylauncher.tree.jre;

import com.google.gson.annotations.SerializedName;

public class JREsManifest {
    @SerializedName("jre") public JreInfo[] jre;

    public JreInfo findJreInfo(String fullVersion) {
        for(JreInfo info : jre) {
            if(info.fullVersion.equals(fullVersion)) return info;
        }

        return null;
    }

    public JreInfo findJreInfoByMajorVersion(int majorVersion) {
        for(JreInfo info : jre) {
            if(info.majorVersion == majorVersion) return info;
        }

        return null;
    }
}