package io.github.spookylauncher.wrapper.util.proxy;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {
    void transform(ClassNode node);
}