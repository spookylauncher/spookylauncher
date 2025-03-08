package io.github.spookylauncher.wrapper.transform;

import io.github.spookylauncher.wrapper.util.proxy.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.util.*;

public final class TDRTransformer implements ClassTransformer {
    private static final Map<String, InstructionsTransformer> METHODS_TRANSFORMERS = new HashMap<>();
    private final String host;
    private final int port;
    private final String directory;

    public TDRTransformer(String host, int port, File directory) {
        this.host = host;
        this.port = port;
        this.directory = directory.getAbsolutePath();
    }

    static {
        METHODS_TRANSFORMERS.put("<init>", TDRTransformer::transformConstructor);
        METHODS_TRANSFORMERS.put("run", TDRTransformer::transformRunMethod);
    }

    private static boolean transformConstructor(
            AbstractInsnNode instruction,
            InstructionsVisitor visitor,
            InsnList instructions,
            ClassTransformer transformer
    ) {
        if(instruction instanceof TypeInsnNode) {
            TypeInsnNode tin = (TypeInsnNode) instruction;

            if
            (
                    tin.desc.equals("java/io/File") &&
                    tin.getOpcode() == Opcodes.NEW &&
                    visitor.peek(1) != null &&
                    visitor.peek(1).getOpcode() == Opcodes.DUP
            ) {
                LdcInsnNode filePathNode = (LdcInsnNode) visitor.peek(3);

                if("resources/".equals(filePathNode.cst)) {
                    MethodInsnNode fileCreatingNode = (MethodInsnNode) visitor.peek(4);

                    instructions.remove(visitor.peek(2));

                    filePathNode.cst = ( (TDRTransformer) transformer ).directory;
                    fileCreatingNode.desc = "(Ljava/lang/String;)V";

                    return true;
                }
            }
        }

        return false;
    }

    private static boolean transformRunMethod(
            AbstractInsnNode instruction,
            InstructionsVisitor visitor,
            InsnList instructions,
            ClassTransformer transformer
    ) {
        if(instruction instanceof TypeInsnNode) {
            TypeInsnNode tin = (TypeInsnNode) instruction;

            if
            (
                    tin.desc.equals("java/net/URL") &&
                    tin.getOpcode() == Opcodes.NEW &&
                    visitor.peek(1) != null &&
                    visitor.peek(1).getOpcode() == Opcodes.DUP
            ) {
                LdcInsnNode resourcesURLNode = (LdcInsnNode) visitor.peek(3);

                final TDRTransformer tdr = (TDRTransformer) transformer;

                resourcesURLNode.cst = "http://" + tdr.host + ":" + tdr.port + "/MinecraftResources/";

                return true;
            }
        }

        return false;
    }

    @Override
    public void transform(ClassNode node) {
        InstructionsVisitor visitor;

        for(MethodNode method : node.methods) {
            if(METHODS_TRANSFORMERS.containsKey(method.name)) {
                InsnList instructions = method.instructions;

                visitor = new InstructionsVisitor(instructions);

                AbstractInsnNode instruction;

                InstructionsTransformer processor = METHODS_TRANSFORMERS.get(method.name);

                while((instruction = visitor.next()) != null) {
                    if(processor.transform(instruction, visitor, instructions, this))
                        break;
                }
            }
        }
    }
}