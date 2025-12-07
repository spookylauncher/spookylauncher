package io.github.spookylauncher.util.github;

import io.github.spookylauncher.util.Json;
import io.github.spookylauncher.util.github.tree.Child;
import io.github.spookylauncher.util.github.tree.Tree;
import io.github.spookylauncher.io.collectors.URLCollector;

import java.io.IOException;
import java.net.URISyntaxException;

public final class GitHubAPI {
    public static Tree getTreeFromBranch(String user, String repo, String path) throws IOException {
        return getTreeFromBranch(user, repo, "main", path);
    }

    public static Tree getTreeFromBranch(String user, String repo, String branch, String path) throws IOException {
        Tree tree = getTree(user, repo, branch);

        if(tree.tree == null) return null;

        for(Child child : tree.tree) {
            if(child.path.equals(path)) {
                try {
                    return Json.collectJson(new URLCollector(child.url), Tree.class);
                } catch (URISyntaxException e) {
                    throw new IOException(e);
                }
            }
        }

        return null;
    }

    public static Tree getTree(String user, String repo, String tree) throws IOException {
        try {
            return Json.collectJson(new URLCollector(
                    "https://api.github.com/repos/" + user + "/" + repo + "/git/trees/" + tree + "?recursive=1"
            ), Tree.class);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}