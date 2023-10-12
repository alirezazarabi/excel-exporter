package com.devalz.excelexporter.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PageUtils {

    public static final int MAX_PAGE_SIZE = 200;
    public static final int EXCEL_MAX_PAGE_SIZE = 10_000;

    private static int validatePagesSize(int size, boolean excel) {
        int maxPageSize = excel ? EXCEL_MAX_PAGE_SIZE : MAX_PAGE_SIZE;
        if (size > maxPageSize) {
            size = maxPageSize;
        }
        return size;
    }

    public static Pageable getInstance(int page, int size, String orderBy, Sort.Direction direction, boolean excel) {
        size = validatePagesSize(size, excel);
        Pageable pageable;
        if (direction != null && StringUtils.hasText(orderBy)) {
            Sort sort = Sort.by(direction, orderBy);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }
        return pageable;
    }
}
