package io.github.spookylauncher.advio.collectors;

import io.github.spookylauncher.advio.Resource;

import java.io.IOException;

public final class ResourceCollector extends StreamCollector {
    public ResourceCollector(String path) throws IOException {
        super(Resource.getInput(path));
    }
}