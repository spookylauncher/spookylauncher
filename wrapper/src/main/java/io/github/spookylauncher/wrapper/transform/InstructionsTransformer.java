package io.github.spookylauncher.wrapper.transform;

import io.github.spookylauncher.wrapper.util.proxy.ClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public interface InstructionsTransformer {
    boolean transform(
            AbstractInsnNode insn,
            InstructionsVisitor visitor,
            InsnList insns,
            ClassTransformer transformer
    );
}