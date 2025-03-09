package io.github.spookylauncher.wrapper.util.proxy;

import io.github.spookylauncher.wrapper.util.ASMUtils;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;

public final class ProxyClassLoader extends ClassLoader {

    private final List<ClassTransformer> transformers = new ArrayList<>();
    private final HashMap<String, Class<?>> loaded = new HashMap<>();

    private final HashSet<String> closedPackages = new HashSet<>();

    private final HashMap<String, List<ClassTransformer>> specializedTransformers = new HashMap<>();

    private final boolean specialized;

    private static final HashSet<String> PROHIBITED_PACKAGES = new HashSet<>();

    static {
        Collections.addAll(PROHIBITED_PACKAGES,
        "java", "org.w3c.dom", "org.ietf.jgss",
                "org.jcp.xml.dsig.internal", "org.omg",
                "org.xml.sax", "jdk", "javax", "com.sun", "com.oracle", "sun", "com.azul");
    }

    public ProxyClassLoader() {
        this(false);
    }

    public ProxyClassLoader(boolean specialized) {
        this(ProxyClassLoader.class.getClassLoader(), specialized);
    }

    public ProxyClassLoader(ClassLoader parent, boolean specialized) {
        super(parent);
        this.specialized = specialized;
        this.closedPackages.addAll(PROHIBITED_PACKAGES);
    }

    public void closePackage(String pkg) {
        this.closedPackages.add(pkg);
    }

    public void addTransformer(ClassTransformer transformer) {
        transformers.add(transformer);
    }

    public void addSpecializedTransformer(String className, ClassTransformer transformer) {
        if(!specialized)
            throw new IllegalStateException("current proxy class loader is not specialized");

        List<ClassTransformer> transformers;

        if(specializedTransformers.containsKey(className))
            transformers = specializedTransformers.get(className);
        else {
            transformers = new ArrayList<>();
            specializedTransformers.put(className, transformers);
        }

        transformers.add(transformer);
    }

    private boolean canModify(String name) {
        if(specialized && !specializedTransformers.containsKey(name)) return false;

        for(String prohibitedPackage : PROHIBITED_PACKAGES) {
            if(name.startsWith(prohibitedPackage + "."))
                return false;
        }

        return true;
    }

    private boolean canDefine(String name) {
        for(String pkg : closedPackages) {
            if(name.startsWith(pkg + "."))
                return false;
        }

        return true;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if(loaded.containsKey(name)) return loaded.get(name);

        if(!canDefine(name))
            return super.loadClass(name);

        String path = name.replace(".", "/") + ".class";

        InputStream in = getParent().getResourceAsStream(path);

        if(in != null) {
            try {
                ByteArrayOutputStream baOs = new ByteArrayOutputStream();

                byte[] buffer = new byte[8096];

                int len;

                while((len = in.read(buffer)) != -1)
                    baOs.write(buffer, 0, len);

                in.close();

                byte[] classBytes = baOs.toByteArray();

                if(canModify(name)) {
                    ClassNode node = ASMUtils.getNode(classBytes);

                    if (node != null) {
                        for (ClassTransformer transformer : transformers)
                            transformer.transform(node);

                        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

                        node.accept(new CheckClassAdapter(writer));

                        classBytes = writer.toByteArray();
                    }
                }

                Class<?> clazz = super.defineClass(name, classBytes, 0, classBytes.length);

                loaded.put(name, clazz);

                return clazz;
            } catch(IOException e) {
                throw new ClassNotFoundException("I/O Exception was occurred: " + e);
            }
        } else throw new ClassNotFoundException(name);
    }
}