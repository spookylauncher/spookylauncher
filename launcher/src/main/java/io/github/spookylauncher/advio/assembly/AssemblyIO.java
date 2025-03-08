package io.github.spookylauncher.advio.assembly;

import io.github.spookylauncher.advio.IOUtils;
import io.github.spookylauncher.advio.collectors.Collector;
import io.github.spookylauncher.advio.peddlers.Peddler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class AssemblyIO {
    private static final AssemblyReader READER = new AssemblyReader();
    private static final AssemblyWriter WRITER = new AssemblyWriter();

    public static Assembly read(Collector collector) {
        return read(collector.collectInput());
    }
    public static Assembly read(InputStream input) {
        try {
            return READER.read(input);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean write(Assembly assembly, Peddler peddler) {
        return write(assembly, peddler.asStream());
    }

    public static boolean write(Assembly assembly, OutputStream output) {
        try {
            WRITER.write(assembly, output);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean assemblyToZip(File assembly, File zip) {
        try {
            assemblyToZip(Collector.of(assembly), Peddler.of(zip));

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean zipToAssembly(File zip, File assembly) {
        try {
            WRITER.write(zipToAssembly(Collector.of(zip)), assembly);

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Assembly zipToAssembly(Collector collector) {
        ZipInputStream zip = new ZipInputStream(collector.collectInput());

        try {
            List<Blob> blobs = new ArrayList<>();

            ZipEntry entry;

            while((entry = zip.getNextEntry()) != null) {
                if(entry.isDirectory()) continue;

                String name = entry.getName();

                if(name.startsWith("/")) name = name.substring(1);

                blobs.add(new Blob(name, IOUtils.readBytes(zip)));

                zip.closeEntry();
            }

            zip.close();

            return new Assembly(blobs);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean assemblyToZip(Collector assemblyCollector, Peddler zipPeddler) {
        try {
            ZipOutputStream zip = new ZipOutputStream(zipPeddler.asStream());
            Assembly assembly = read(assemblyCollector);

            for(Blob blob : assembly.getBlobs()) {
                ZipEntry entry = new ZipEntry(blob.name);
                entry.setSize(blob.data.length);
                entry.setTime(0);

                zip.putNextEntry(entry);
                zip.write(blob.data);
                zip.closeEntry();
            }

            zip.close();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}