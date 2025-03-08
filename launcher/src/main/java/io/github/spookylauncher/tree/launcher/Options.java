package io.github.spookylauncher.tree.launcher;

import io.github.spookylauncher.tree.jre.SelectedJavaType;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;
public final class Options {
    @SerializedName("memory") public int memory = 1024;

    @SerializedName("discordPresence") public boolean discordPresence = true;

    @SerializedName("selectedJavaType") public SelectedJavaType selectedJavaType = SelectedJavaType.EXTERNAL;

    @SerializedName("selectedJava") public String selectedJava;
    @SerializedName("customJavaPath") public String customJavaPath;
    @SerializedName("selectedJavaPath") public String selectedJavaPath;
    @SerializedName("nickname") private String nickname;
    @SerializedName("selectedVersion") public String selectedVersion;
    @SerializedName("locale") public String locale = Locale.getDefault().getLanguage();


    public String getNickname() {
        return nickname == null ? "" : nickname;
    }

    public void setNickname(String nickname) {
        if(nickname == null || nickname.isEmpty()) this.nickname = null;
        else this.nickname = nickname;
    }
}