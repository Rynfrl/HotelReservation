package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {
    public static LocalDate parse(String s) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(s, f);
    }

    public static String format(LocalDate d) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return d.format(f);
    }
}
