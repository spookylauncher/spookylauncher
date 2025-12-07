package io.github.spookylauncher.util;

import com.google.gson.annotations.SerializedName;

import java.time.*;

public class GeneralDate {
    @SerializedName("year") public int year;
    @SerializedName("month") public int month;
    @SerializedName("day") public int day;

    public GeneralDate() {}

    public GeneralDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String toString() {
        return year + "/" + month + "/" + day;
    }

    public long toTimestamp() { return LocalDateTime.of(year, month, day, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli(); }
}