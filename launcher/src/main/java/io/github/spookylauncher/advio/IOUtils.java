package io.github.spookylauncher.advio;

import com.sun.management.OperatingSystemMXBean;
import io.github.spookylauncher.advio.collectors.Collector;

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

    public static List<String> readLines(InputStream in) {
        try {
            List<String> lines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            String line;

            while((line = reader.readLine()) != null) lines.add(line);

            reader.close();

            return lines;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String readString(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();

            int character;

            while((character = reader.read()) != -1) builder.append((char) character);

            reader.close();
            return builder.toString();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readBytes(InputStream in) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = in.read(data, 0, data.length)) != -1) buffer.write(data, 0, nRead);

            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean move(InstallAdapter adapter, int length, InputStream in, OutputStream out) throws IOException {
        boolean cancel = false;

        boolean hasCancelSupplier = adapter.cancelSupplier != null;
        boolean hasProgressConsumer = adapter.progressConsumer != null;

        int updateCounterLimit = length / 100;
        byte supplierCounter = 0;
        int len;
        int counter = 0;
        int updateCounter = 0;

        while((len = in.read()) != -1) {
            out.write(len);

            counter++;

            if(updateCounter++ == updateCounterLimit) {
                updateCounter = 0;

                if(hasProgressConsumer) adapter.progressConsumer.accept(counter);

                if(++supplierCounter == 5) {
                    supplierCounter = 0;

                    if(hasCancelSupplier && adapter.cancelSupplier.get()) {
                        cancel = true;
                        break;
                    }
                }
            }
        }

        return !cancel;
    }
    public static boolean install(Collector collector, File dest, InstallAdapter fns) {
        try {
            BufferedInputStream in = new BufferedInputStream(collector.collectInput());

            int length = (int) collector.size();

            BufferedOutputStream out;
            FileOutputStream fout;

            boolean cancel;

            fns.progressBarMaxConsumer.accept(length);

            out = new BufferedOutputStream(fout = new FileOutputStream(dest));

            cancel = !move(fns, length, in, out);

            out.flush();
            out.close();
            fout.close();

            in.close();

            if(cancel && fns.onCancel != null) fns.onCancel.run();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unzip(Collector zip, File dest, InstallAdapter fns) {
        try {
            ZipInputStream in = new ZipInputStream(new BufferedInputStream(zip.collectInput(), 8192 * 5));

            ZipEntry e;

            int length;
            String name;

            BufferedOutputStream out;
            FileOutputStream fout;
            File ef;

            boolean cancel = false;
            boolean hasTitleConsumer = fns.titleConsumer != null;

            while((e = in.getNextEntry()) != null) {
                ef = new File(dest, e.getName());

                if(e.isDirectory()) {
                    ef.mkdirs();
                    continue;
                }

                length = (int) e.getSize();

                fns.progressBarMaxConsumer.accept(length);

                if(hasTitleConsumer) {
                    name = e.getName();

                    if(!fns.consumeFullPaths) name = name.substring(name.lastIndexOf("/") + 1);

                    fns.titleConsumer.accept(name);
                }

                ef.getParentFile().mkdirs();

                out = new BufferedOutputStream(fout = new FileOutputStream(ef));

                cancel = !move(fns, length, in, out);

                out.flush();
                out.close();
                fout.close();
                in.closeEntry();

                if(cancel) break;
            }

            in.close();

            if(cancel && fns.onCancel != null) fns.onCancel.run();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void deleteTree(File... files) {
        try {
            for(File file : files) {
                if(!file.exists()) continue;

                if(file.isFile()) file.delete();
                else {
                    deleteTree(file.listFiles());

                    file.delete();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean copy(File src, File dest) {
        try {
            FileInputStream fin;
            FileOutputStream fout;

            BufferedInputStream in = new BufferedInputStream(fin = new FileInputStream(src));
            BufferedOutputStream out = new BufferedOutputStream(fout = new FileOutputStream(dest));

            int len;

            while((len = in.read()) != -1) out.write(len);

            in.close();
            out.flush();
            out.close();
            fin.close();
            fout.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPathWithoutFormat(String path) {
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

    public static String[] locate(String processName) {
        List<String> result = new ArrayList<>();

        try {
            String commandName = "";

            switch(Objects.requireNonNull(OSType.CURRENT)) {
                case WINDOWS: commandName = "where"; break;
                case MACOS: commandName = "which"; break;
                case LINUX: commandName = "locate"; break;
                default: throw new RuntimeException("Unknown OS: " + OSType.CURRENT);
            }

            Process process = Runtime.getRuntime().exec(commandName + " " + processName);

            if(process.waitFor() == 0) result.addAll(readLines(process.getInputStream()));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return result.toArray(new String[0]);
    }
    public static File find(String ending, File... dirs) {
        return find(ending, true, dirs);
    }
    public static File find(String ending, boolean formatSensitivity, File...dirs) {
        return find(ending, formatSensitivity, dirs[0].toPath(), dirs);
    }
    public static File find(String ending, boolean formatSensitivity, Path rootPath, File...dirs) {
        try {
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

                        if(!formatSensitivity) path = getPathWithoutFormat(path);

                        if (path.endsWith(ending)) return file;
                    }
                    else return find(ending, formatSensitivity, rootPath, file);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
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


    public static boolean canWrite(File file) {
        return Files.isWritable(file.toPath());
    }

    public static boolean canRead(File file) {
        return Files.isReadable(file.toPath());
    }

    public static String getExecutableFormat() {
        if (OSType.CURRENT == OSType.WINDOWS) return ".exe";
        return "";
    }

    public static int getNumberOfProcesses(String name) {
        int count = 0;

        for(String process : getProcessList()) {
            if(name.equals(process)) count++;
        }

        return count;
    }

    public static List<String> getProcessList() {
        List<String> list = new ArrayList<>();

        try {
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

                default: throw new UnsupportedOperationException("Unsupported OS: " + OSType.CURRENT);
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
        } catch(Exception e) {
            e.printStackTrace();
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
        try {
            return new RandomAccessFile(file, "rw").getChannel().tryLock();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}