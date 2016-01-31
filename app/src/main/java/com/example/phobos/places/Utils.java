package com.example.phobos.places;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

    private Utils() {}

    public static String formatDate(String x) {
        String dateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date date = strToDate(x);
        if (date != null) {
            dateStr = sdf.format(date);
        }
        return dateStr;
    }

    public static String dateToStr(Date date) {
        String dateStr = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        if (date != null) dateStr = sdf.format(date);
        return dateStr;
    }

    public static Date strToDate(String x) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        if (x != null) {
            x = x.replace("Z", "+00:00");
            try {
                date = sdf.parse(x);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }
}
