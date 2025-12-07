package io.github.spookylauncher.tree.jre;

public class ExternalJreInfo extends JreInfo {
    public final String path;

    public ExternalJreInfo(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ExternalJreInfo)) return false;

        return path.equals(((ExternalJreInfo)obj).path);
    }

    public String toString() {
        return super.toString() + " (" + this.path + ")";
    }
}