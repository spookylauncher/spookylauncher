package io.github.spookylauncher.io.collectors;

import io.github.spookylauncher.io.Resource;

import java.io.IOException;

public final class ResourceCollector extends StreamCollector {
    public ResourceCollector(String path) throws IOException {
        super(Resource.getInput(path));
    }
}