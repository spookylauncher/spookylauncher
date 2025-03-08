package io.github.spookylauncher.wrapper.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.*;
import java.util.Collection;

public class ASMUtils {

    public static MethodNode findMethod(Collection<MethodNode> col, String name, String desc) {
        for(MethodNode node : col) {
            if(name.equals(node.name) && desc.equals(node.desc)) return node;
        }

        return null;
    }

    public static ClassNode getNode(byte[] bytes) {
        ClassNode node = new ClassNode();
        if(!acceptVisitor(node, bytes)) return null;
        else return node;
    }

    public static boolean acceptVisitor(ClassVisitor visitor, byte[] bytes) {
        try {
            ClassReader reader = new ClassReader(bytes);
            reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}