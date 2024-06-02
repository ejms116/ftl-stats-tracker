package net.gausman.ftl.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String formatInstant(Instant instant){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }
}
