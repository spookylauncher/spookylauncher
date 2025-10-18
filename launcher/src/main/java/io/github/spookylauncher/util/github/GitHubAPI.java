package io.github.spookylauncher.util.github;

import io.github.spookylauncher.util.io.Json;
import io.github.spookylauncher.util.github.tree.Child;
import io.github.spookylauncher.util.github.tree.Tree;
import io.github.spookylauncher.advio.collectors.URLCollector;

import java.io.IOException;

public final class GitHubAPI {
    public static Tree getTreeFromBranch(String user, String repo, String path) throws IOException {
        return getTreeFromBranch(user, repo, "main", path);
    }

    public static Tree getTreeFromBranch(String user, String repo, String branch, String path) throws IOException {
        Tree tree = getTree(user, repo, branch);

        if(tree.tree == null || tree.tree.length == 0) return null;

        for(Child child : tree.tree) {
            if(child.path.equals(path)) return Json.collectJson(new URLCollector(child.url), Tree.class);
        }

        return null;
    }

    public static Tree getTree(String user, String repo, String tree) throws IOException {
        return Json.collectJson(new URLCollector(
                "https://api.github.com/repos/" + user + "/" + repo + "/git/trees/" + tree + "?recursive=1"
        ), Tree.class);
    }
}