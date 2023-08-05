package com.hoangdp.todo.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String instantToString(Instant time) {
        return DateTimeFormatter.ofPattern(TimeUtils.PATTERN_FORMAT).withZone(ZoneOffset.of("+07:00"))
                .format(time);
    }

    public static Instant instantFromString(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeUtils.PATTERN_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(timeString, formatter);
        return localDateTime.toInstant(ZoneOffset.of("+07:00"));
    }
}
