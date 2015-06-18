package com.neerpoints.migrations.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    private static long _timestampOffset = 0;

    public static Date dateNow() {
        Date date = new Date();
        date.setTime(dateNowInMillisecondsSince1970());
        return date;
    }

    private static long dateNowInMillisecondsSince1970() {
        return new Date().getTime() - _timestampOffset;
    }

    public static Date parseDate(String dateString, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse the string " + dateString + " using format " + formatString);
        }
    }

    public static String formatDate(Date date, String format) {
        return formatDate(date, format, null);
    }

    public static String formatDate(Date date, String format, TimeZone zone) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        if (zone != null) {
            formatter.setTimeZone(zone);
        }
        return formatter.format(date);
    }
}
