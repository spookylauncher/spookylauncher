package io.github.spookylauncher.components;

import io.github.spookylauncher.advio.IOUtils;
import io.github.spookylauncher.GameStartData;
import io.github.spookylauncher.advio.AsyncOperation;
import io.github.spookylauncher.log.Level;
import io.github.spookylauncher.log.Logger;
import io.github.spookylauncher.util.structures.tuple.Tuple2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.*;
import java.util.*;

public final class LogsController extends LauncherComponent {
    private final File launcherDirectory;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogsController(final ComponentsController components, final File launcherDirectory) {
        super("Logs Controller", components);
        this.launcherDirectory = launcherDirectory;
    }

    public File createNewLog(File logsDirectory) {
        if(!logsDirectory.exists()) logsDirectory.mkdirs();

        File log = new File(logsDirectory, new SimpleDateFormat("yyyy-MM-dd hh-mm").format(new Date()) + ".log");

        try {
            if(!log.createNewFile()) return null;
            else return log;
        } catch(Exception e) {
            components.get(ErrorHandler.class).handleException("startError", e);
            return null;
        }
    }

    public void startLogging(GameStartData data, File log, File latestLog, Runnable onLoggingEnded) {
        List<Tuple2<String, String>> list = new ArrayList<>();

        list.add(Tuple2.of("Version", data.version));
        list.add(Tuple2.of("Nickname", data.nickname));
        list.add(Tuple2.of("Command", data.command));
        list.add(Tuple2.of("Java Major Version", String.valueOf(data.javaMajorVersion)));
        list.add(Tuple2.of("Java Version", data.javaVersion));
        list.add(Tuple2.of("Java Path", data.javaPath));
        list.add(Tuple2.of("Java Vendor", data.javaVendor));

        startLogging(data.uptime, data.process, list, log, latestLog, onLoggingEnded);
    }

    public void startLogging(long uptime, Process process, List<Tuple2<String, String>> data, File log, File latestLog, Runnable onLoggingEnded) {
        AsyncOperation.run(
                () -> {
                    try {
                        InputStream in = process.getInputStream();
                        OutputStream out = Files.newOutputStream(log.toPath());

                        StringBuilder header = new StringBuilder();

                        header.append("----------------------------------------------------------------\n");
                        header.append("\nUptime: ");
                        header.append(dateFormat.format(new Date(uptime)));
                        header.append('\n');

                        for(Tuple2<String, String> tuple : data) {
                            header.append(tuple.x);
                            header.append(": ");
                            header.append(tuple.y);
                            header.append('\n');
                        }

                        header.append("\n----------------------------------------------------------------\n\n\n");

                        out.write(header.toString().getBytes(StandardCharsets.UTF_8));

                        int len;

                        while(process.isAlive()) {

                            while((len = in.read()) != -1) out.write(len);
                        }

                        out.flush();
                        out.close();

                        IOUtils.copy(log, latestLog);

                        onLoggingEnded.run();

                        if(components.get(OptionsController.class).getOptions().discordPresence)
                            components.get(DiscordPresenceViewer.class).showMenuPresence();

                    } catch(Exception e) {
                        components.get(ErrorHandler.class).handleException("logCreationFailed", e);
                    }
                }
        );
    }

    @Override
    public void initialize() {
        super.initialize();

        this.branchLoggerOutput();
    }

    private void branchLoggerOutput() {
        try {
            File logsDirectory = new File(this.launcherDirectory, "logs");

            if(!logsDirectory.exists() && !logsDirectory.mkdirs()) {
                log(Level.ERROR, "failed to create launcher logs directory");
                return;
            }

            final File log = createNewLog(logsDirectory);

            if(log == null) {
                log(Level.ERROR, "failed to create launcher log file");
                return;
            }

            final File latestLog = new File(logsDirectory, "latest.log");

            final PrintStream fileOut = new PrintStream(latestLog, "UTF-8");

            Logger.out.addPrinter(fileOut);
            Logger.err.addPrinter(fileOut);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                fileOut.close();
                IOUtils.copy(latestLog, log);
            }));
        } catch(Exception e) {
            log(Level.ERROR, "failed to branch logger output");
            e.printStackTrace();
        }
    }
}