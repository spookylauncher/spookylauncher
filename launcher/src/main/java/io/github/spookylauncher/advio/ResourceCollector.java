package io.github.spookylauncher.advio;

import io.github.spookylauncher.advio.collectors.StreamCollector;

public final class ResourceCollector extends StreamCollector {
    public ResourceCollector(String path) {
        super(Resource.getInput(path));
    }
}