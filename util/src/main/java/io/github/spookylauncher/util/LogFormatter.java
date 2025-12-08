package io.github.spookylauncher.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LogFormatter extends Formatter {

    private static final int LOGGER_FIELD_LENGTH = 25;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSZ");

    private String formatLoggerName(String loggerName) {
        StringBuilder sb = new StringBuilder();

        int spacesCount = LOGGER_FIELD_LENGTH - loggerName.length();

        for(int i = 0; i < spacesCount; i++)
            sb.append(" ");

        sb.append(loggerName);

        return sb.toString();
    }

    @Override
    public String format(LogRecord record) {
        return "[" + record.getLevel() + "] " +
                Instant.ofEpochMilli(record.getMillis())
                        .atZone(ZoneId.of("Europe/Moscow"))
                        .format(formatter) +
                " [" + formatLoggerName(record.getLoggerName()) + "] " +
                formatMessage(record) +
                System.lineSeparator();
    }
}