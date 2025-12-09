package io.github.spookylauncher.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LogFormatter extends Formatter {

    private static final int LOGGER_FIELD_LENGTH = 25;
    private static final int LEVEL_FIELD_LENGTH = 6;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSZ");

    private String formatField(int length, String loggerName) {
        StringBuilder sb = new StringBuilder();

        int spacesCount = length - loggerName.length();

        for(int i = 0; i < spacesCount; i++)
            sb.append(" ");

        sb.append(loggerName);

        return sb.toString();
    }

    @Override
    public String format(LogRecord record) {
        String res = "[" + formatField(LEVEL_FIELD_LENGTH, record.getLevel().getName()) + "] " +
                Instant.ofEpochMilli(record.getMillis())
                        .atZone(ZoneId.of("Europe/Moscow"))
                        .format(formatter) +
                " [" + formatField(LOGGER_FIELD_LENGTH, record.getLoggerName()) + "] " +
                formatMessage(record);

        if(record.getThrown() != null) {
            res += " : " + record.getSourceClassName() + "." +
                    record.getSourceMethodName() + "(): " +
                    record.getThrown().toString();
        }

        return res + System.lineSeparator();
    }
}