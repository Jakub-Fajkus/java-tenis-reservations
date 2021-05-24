package cz.jakubfajkus.reservations.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsoDateFormatter {

    public static String format(LocalDateTime dateFrom) {
        return dateFrom.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
