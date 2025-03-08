package io.github.spookylauncher.util.io;

import io.github.spookylauncher.advio.collectors.Collector;

public  class RealtimeClassLoader extends ClassLoader {
    public Class<?> loadClass(Collector collector) {
        return this.loadClass(collector.collectBytes());
    }
    public Class<?> loadClass(byte[] bytes) {
        return super.defineClass(null, bytes, 0, bytes.length);
    }
}