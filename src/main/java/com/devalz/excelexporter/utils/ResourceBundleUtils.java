package com.devalz.excelexporter.utils;

import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class ResourceBundleUtils {

    public static String getMessage(String code) {
        Assert.notNull(code, "code is null");
        return SpringContext.getBean(MessageSource.class).getMessage(code, null, LocaleUtils.getInstance());
    }

    public static String getMessage(String code, Object... args) {
        Assert.notNull(code, "code is null");
        return SpringContext.getBean(MessageSource.class).getMessage(code, args, LocaleUtils.getInstance());
    }
}
