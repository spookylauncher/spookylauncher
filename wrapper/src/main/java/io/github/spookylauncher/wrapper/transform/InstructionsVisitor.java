package io.github.spookylauncher.wrapper.transform;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class InstructionsVisitor {
    private final InsnList instructions;
    private int pos = -1;

    public InstructionsVisitor(InsnList instructions) {
        this.instructions = instructions;
    }

    public AbstractInsnNode peek(int relate) {
        if(instructions == null)
            throw new IllegalStateException("instructions list is not set");

        return instructions.get(pos + relate);
    }

    public AbstractInsnNode next() {
        pos++;
        return peek(0);
    }

    public AbstractInsnNode prev() {
        pos--;
        return peek(0);
    }
}