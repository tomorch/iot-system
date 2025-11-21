package com.iot.sensor.simulator.utils;

import java.time.format.DateTimeFormatter;

public class DateFormat {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String format(java.time.LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
