package com.devalz.excelexporter.utils;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_PATTERN = "yyyy/MM/dd";
    private static final String DATE_TIME_PATTERN = "yyyy/MM/dd - HH:mm:ss";

    public static String currentDateTimeInString(String pattern, String numberFormatLocale) {
        SimpleDateFormat df = new SimpleDateFormat(pattern, LocaleUtils.getInstance());
        df.setNumberFormat(NumberFormat.getNumberInstance(new Locale(numberFormatLocale)));
        return df.format(new Date());
    }

    public static String convertToString(LocalDateTime localDateTime, String pattern, String numberFormatLocale) {
        Assert.notNull(localDateTime, "localDateTime is null");
        Assert.notNull(pattern, "pattern is null");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat df = new SimpleDateFormat(pattern, LocaleUtils.getInstance());
        if (StringUtils.hasText(numberFormatLocale)) {
            df.setNumberFormat(NumberFormat.getNumberInstance(new Locale(numberFormatLocale)));
        }
        return df.format(date);
    }

    public static String convertToString(LocalDateTime localDateTime, String pattern) {
        return convertToString(localDateTime, pattern, null);
    }

    public static String convertToDateFormat(LocalDateTime localDateTime) {
        return convertToString(localDateTime, DATE_PATTERN, null);
    }

    public static String convertToDateTimeFormat(LocalDateTime localDateTime) {
        return convertToString(localDateTime, DATE_TIME_PATTERN, null);
    }
}
