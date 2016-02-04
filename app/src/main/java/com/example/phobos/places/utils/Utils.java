package com.example.phobos.places.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";

    private Utils() {}

    public static String formatDate(String x) {
        Date date = strToDate(x);
        return formatDate(date);
    }

    public static String formatDate(Date date) {
        String dateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.ENGLISH);
        if (date != null) {
            dateStr = sdf.format(date);
        }
        return dateStr;
    }

    public static String dateToStr(Date date) {
        String dateStr = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        if (date != null) {
            dateStr = sdf.format(date);
        }
        return dateStr;
    }

    public static Date strToDate(String x) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        if (x != null) {
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
