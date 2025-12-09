package io.github.spookylauncher.io;

import com.sun.management.OperatingSystemMXBean;
import io.github.spookylauncher.io.collectors.Collector;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.zip.*;

public final class IOUtils {
    public static final long MAX_PHYSICAL_MEMORY = getPhysicalMemory();

    private static long getPhysicalMemory() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }

    public static List<String> readLines(InputStream in) throws IOException {
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line;

        while((line = reader.readLine()) != null)
            lines.add(line);

        reader.close();

        return lines;
    }

    public static String readString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();

        int character;

        while((character = reader.read()) != -1)
            builder.append((char) character);

        reader.close();

        return builder.toString();
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = in.read(data, 0, data.length)) != -1)
            buffer.write(data, 0, nRead);

        return buffer.toByteArray();
    }

    private static boolean move(InstallAdapter adapter, InputStream in, OutputStream out) throws IOException {
        boolean cancel = false;

        boolean hasCancelSupplier = adapter.cancelSupplier != null;
        boolean hasProgressConsumer = adapter.progressConsumer != null;
        int len;
        int counter = 0;

        byte[] buffer = new byte[4096];

        while((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);

            counter += len;

            if(hasCancelSupplier && adapter.cancelSupplier.get()) {
                cancel = true;
            } else if(hasProgressConsumer) adapter.progressConsumer.accept(counter);
        }

        return cancel;
    }

    public static void install(Collector collector, File dest, InstallAdapter fns) throws IOException {
        InputStream in = collector.collectInput();

        int length = (int) collector.size();

        OutputStream out;

        boolean cancel;

        fns.progressBarMaxConsumer.accept(length);

        out = Files.newOutputStream(dest.toPath());

        cancel = move(fns, in, out);

        out.flush();
        out.close();

        in.close();

        if(cancel && fns.onCancel != null)
            fns.onCancel.run();
    }

    public static void unzip(Collector zip, File dest, InstallAdapter fns) throws IOException {
        ZipInputStream in = new ZipInputStream(new BufferedInputStream(zip.collectInput(), 16384));

        ZipEntry e;

        String name;

        OutputStream out;
        File ef;

        boolean cancel = false;
        boolean hasTitleConsumer = fns.titleConsumer != null;

        while((e = in.getNextEntry()) != null) {
            ef = new File(dest, e.getName());

            if(e.isDirectory()) {
                if(!ef.exists() && !ef.mkdirs())
                    throw new IOException("Failed to create directory " + ef.getAbsolutePath());
                continue;
            }

            fns.progressBarMaxConsumer.accept((int) e.getSize());

            if(hasTitleConsumer) {
                name = e.getName();

                if(!fns.consumeFullPaths) name = name.substring(name.lastIndexOf("/") + 1);

                fns.titleConsumer.accept(name);
            }

            out = Files.newOutputStream(ef.toPath());

            cancel = move(fns, in, out);

            out.flush();
            out.close();
            out.close();
            in.closeEntry();

            if(cancel) break;
        }

        in.close();

        if(cancel && fns.onCancel != null) fns.onCancel.run();
    }

    public static void deleteTree(File... files) throws IOException {
        for(File file : files) {
            if(!file.exists()) continue;

            if(file.isDirectory()) {
                File[] dirFiles = file.listFiles();

                if(dirFiles != null)
                    deleteTree(dirFiles);
            }

            if(!file.delete())
                throw new IOException("Failed to delete file " + file.getAbsolutePath());
        }
    }

    public static void copy(File src, File dest) throws IOException {
        InputStream in = Files.newInputStream(src.toPath());
        OutputStream out = Files.newOutputStream(dest.toPath());

        byte[] buffer = new byte[16384];

        int len;

        while((len = in.read(buffer)) != -1)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    public static String getPathWithoutExtension(String path) {
        path = path.replace("\\", "/");

        if
        (
                (
                    path.contains("/")
                    && !path.substring(path.lastIndexOf("/") + 1).contains(".")
                )
        ) return path;
        else if(!path.contains(".")) return path;
        else return path.substring(0, path.lastIndexOf("."));
    }

    public static String[] locate(String processName) throws IOException, InterruptedException {
        List<String> result = new ArrayList<>();

        String commandName;

        switch(Objects.requireNonNull(OSType.CURRENT)) {
            case WINDOWS: commandName = "where"; break;
            case MACOS: commandName = "which"; break;
            case LINUX: commandName = "locate"; break;
            default: throw new IOException("Unknown OS: " + OSType.CURRENT);
        }

        Process process = Runtime.getRuntime().exec(commandName + " " + processName);

        if(process.waitFor() == 0) result.addAll(readLines(process.getInputStream()));

        return result.toArray(new String[0]);
    }

    public static File find(String ending, File... dirs) {
        return find(ending, true, dirs);
    }
    public static File find(String ending, boolean formatSensitivity, File...dirs) {
        return find(ending, formatSensitivity, dirs[0].toPath(), dirs);
    }

    public static File find(String ending, boolean formatSensitivity, Path rootPath, File...dirs) {
        for(File dir : dirs) {
            File[] files = dir.listFiles();

            if(files == null) continue;

            for(File file : files) {
                if(file.isFile()) {
                    String path =
                        rootPath
                        .relativize(file.toPath())
                        .toString()
                        .replace("\\", "/");

                    if(!formatSensitivity) path = getPathWithoutExtension(path);

                    if (path.endsWith(ending)) return file;
                }
                else return find(ending, formatSensitivity, rootPath, file);
            }
        }

        return null;
    }

    public static File getLeftmostParent(File file) {
        File parent = file.getParentFile();

        if(parent == null) return file;
        else return getLeftmostParent(parent);
    }

    public static String getRelativePath(File currentDirF, File absoluteDirF) {
        return currentDirF.toPath().relativize(absoluteDirF.toPath()).toString();
    }

    public static String getExecutableFormat() {
        if (OSType.CURRENT == OSType.WINDOWS) return ".exe";
        return "";
    }

    public static int getNumberOfProcesses(String name) throws IOException {
        int count = 0;

        for(String process : getProcessList()) {
            if(name.equals(process)) count++;
        }

        return count;
    }

    public static List<String> getProcessList() throws IOException {
        List<String> list = new ArrayList<>();

        String processName;

        switch(OSType.CURRENT) {
            case WINDOWS:
                processName = "tasklist.exe /fo csv /nh";
                break;

            case LINUX:
                processName = "ps -e";
                break;

            case MACOS:
                processName = "ps -T -e";
                break;

            default: throw new IOException("Unsupported OS: " + OSType.CURRENT);
        }

        Process p = Runtime.getRuntime().exec(processName);

        List<String> lines = readLines(p.getInputStream());

        BiFunction<String, Integer, String> nameExtractFunction;

        AtomicInteger tempIndex = new AtomicInteger();

        switch(OSType.CURRENT) {
            case WINDOWS:
                nameExtractFunction = (line, i) -> {
                    line = line.substring(1);

                    return line.substring(0, line.indexOf('"'));
                };
                break;

            case MACOS: case LINUX:
                nameExtractFunction = (line, i) -> {
                    String path;

                    if(i == 0) {
                        int index = line.indexOf("CMD");

                        if(index == -1) {
                            index = 0;

                            String[] splits = line.split(" ");

                            for(int j = 0;j < 6;j++) {
                                index += splits[j].length();

                                if(j != 5) index += 1;
                            }

                            tempIndex.set(index);

                            path = splits[6];
                        } else {
                            tempIndex.set(index);
                            path = null;
                        }
                    }
                    else path = line.substring(tempIndex.get());

                    return path == null ? null : (path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path);
                };
                break;

            default: throw new UnsupportedOperationException("Unsupported OS: " + OSType.CURRENT);
        }

        for(int i = 0;i < lines.size();i++) {
            String name = nameExtractFunction.apply(lines.get(i), i);

            if(name != null) list.add(name);
        }

        return list;
    }

    public static boolean isLocked(File file) throws IOException {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.close();

            FileInputStream in = new FileInputStream(file);
            in.close();
        } catch(FileNotFoundException e) {
            if(file.exists()) return true;
        }

        FileLock lock = lock(file);

        if(lock == null) return true;
        else {
            lock.close();
            return false;
        }
    }

    public static FileLock lock(File file) throws IOException {
        return new RandomAccessFile(file, "rw").getChannel().tryLock();
    }
}