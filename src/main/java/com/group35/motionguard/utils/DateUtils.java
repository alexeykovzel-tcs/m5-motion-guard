package com.group35.motionguard.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final Date MIN_DATE = new Date(0);

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Date shiftMinutes(Date date, int minutes) {
        return shiftTime(date, minutes * 60 * 1000L);
    }

    public static Date shiftTime(Date date, long time) {
        return new Date(date.getTime() + time);
    }

    public static String toString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
