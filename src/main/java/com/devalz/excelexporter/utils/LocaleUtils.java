package com.devalz.excelexporter.utils;


import com.devalz.excelexporter.exception.ServiceException;
import org.springframework.core.env.Environment;

import java.util.Locale;

public class LocaleUtils {

    public static Locale getInstance() {
        String locale = SpringContext.getBean(Environment.class).getProperty("app.locale");
        if (locale == null) {
            throw new ServiceException("error.localeUtils.localeNotSetInConfig");
        }
        return new Locale(locale);
    }
}
