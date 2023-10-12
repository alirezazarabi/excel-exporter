package com.devalz.excelexporter.utils.excel;

import com.devalz.excelexporter.exception.ServiceException;
import com.devalz.excelexporter.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExcelExporter<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExporter.class);

    private static final String DEFAULT_EXCEL_FILE_NAME = "excel";
    private static final String FONT = "Calibri";
    private static final String SHEET_NAME = "Sheet1";

    private XSSFWorkbook workbook;
    private XSSFFont headerFont;
    private XSSFFont cellFont;
    private CellStyle headerStyle;
    private CellStyle cellStyle;
    private DataFormat dataFormat;
    private Map<Integer, CellStyle> columnStyles = new HashMap<>();

    private String fileName;
    private List<T> dataList;
    private List<ExcelColumn<T>> excelColumnList;

    private ExcelExporter() {
    }

    public String getFileName() {
        return fileName;
    }

    public static class Builder<T> {

        private String fileName;
        private List<T> dataList;
        private final List<ExcelColumn<T>> excelColumnList = new ArrayList<>();

        public Builder<T> fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder<T> dataList(List<T> dataList) {
            this.dataList = dataList;
            return this;
        }

        public ExcelColumn.Builder<T> addColumn() {
            return new ExcelColumn.Builder<>(this, excelColumnList::add);
        }

        public ExcelExporter<T> build() {
            validateProperty();
            ExcelExporter<T> excelExporter = new ExcelExporter<>();
            excelExporter.fileName = createFileName(this.fileName);
            excelExporter.dataList = this.dataList;
            excelExporter.excelColumnList = this.excelColumnList;
            return excelExporter;
        }

        private void validateProperty() {
            if (CollectionUtils.isEmpty(this.dataList)) {
                throw new IllegalArgumentException("dataList is null or empty");
            }
            if (CollectionUtils.isEmpty(this.excelColumnList)) {
                throw new IllegalArgumentException("excelColumnList is null or empty");
            }
        }

        private String createFileName(String fileName) {
            if (StringUtils.isBlank(fileName)) {
                fileName = DEFAULT_EXCEL_FILE_NAME;
            }
            String currentDateTime =
                    DateUtils.currentDateTimeInString("yyyyMMdd_HHmmss", "en");
            return String.format("%s_%s.xlsx", fileName, currentDateTime);
        }
    }

    public byte[] export() {
        byte[] content;
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            init(workbook);
            Sheet sheet = createAndConfigSheet();
            writeHeaderRow(sheet);
            writeRows(sheet);
            workbook.write(outputStream);
            content = outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("error occurred in creating excel");
            logger.error(e.getMessage(), e);
            throw new ServiceException("error.excelExporter.errorInGenerateExcelFile");
        }
        return content;
    }

    private void init(XSSFWorkbook workbook) {
        Assert.notNull(workbook, "workbook is null");
        this.workbook = workbook;
        this.headerFont = getHeaderFont(workbook);
        this.cellFont = getCellFont(workbook);
        this.headerStyle = getHeaderStyle(workbook, headerFont);
        this.cellStyle = getCellStyle(workbook, cellFont);
        this.dataFormat = workbook.createDataFormat();
    }

    private Sheet createAndConfigSheet() {
        Sheet sheet = workbook.createSheet(SHEET_NAME);
        sheet.setRightToLeft(true);
        sheet.setDefaultColumnWidth(20);
        return sheet;
    }

    private void writeHeaderRow(Sheet sheet) {
        int headerRowIndex = 0;
        Row headerRow = sheet.createRow(0);
        for (ExcelColumn<T> excelColumn : excelColumnList) {
            Cell headerCell = headerRow.createCell(headerRowIndex);
            headerCell.setCellValue(excelColumn.getHeaderName());
            headerCell.setCellStyle(headerStyle);
            headerRowIndex++;
        }
    }

    private void writeRows(Sheet sheet) {
        int rowIndex = 1;
        for (T data : dataList) {
            Row row = sheet.createRow(rowIndex);
            int columnIndex = 0;
            for (ExcelColumn<T> excelColumn : excelColumnList) {
                Function<T, ?> function = excelColumn.getSource();
                Object value = function.apply(data);
                Cell cell = row.createCell(columnIndex);
                CellStyle columnCellStyle = getColumnCellStyle(columnIndex, excelColumn.getDataFormat());
                cell.setCellStyle(columnCellStyle);
                setCellValue(value, excelColumn.getType(), cell);
                columnIndex++;
            }
            rowIndex++;
        }
    }

    private CellStyle getColumnCellStyle(int columnIndex, String columnDataFormat) {
        if (columnStyles.containsKey(columnIndex)) {
            return columnStyles.get(columnIndex);
        }
        if (StringUtils.isNotBlank(columnDataFormat)) {
            CellStyle columnCellStyle = workbook.createCellStyle();
            columnCellStyle.cloneStyleFrom(this.cellStyle);
            columnCellStyle.setDataFormat(dataFormat.getFormat(columnDataFormat));
            columnStyles.put(columnIndex, columnCellStyle);
        } else {
            columnStyles.put(columnIndex, this.cellStyle);
        }
        return columnStyles.get(columnIndex);
    }

    private void setCellValue(Object value, ExcelColumn.Type type, Cell cell) {
        Assert.notNull(type, "type is null");
        Assert.notNull(cell, "cell is null");
        if (value == null) {
            cell.setCellValue(StringUtils.EMPTY);
            return;
        }
        switch (type) {
            case NUMERIC:
                cell.setCellValue(Double.parseDouble(value.toString()));
                break;
            case BOOLEAN:
                cell.setCellValue((Boolean) value);
                break;
            default:
                cell.setCellValue(value.toString());
        }

    }

    private XSSFFont getHeaderFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName(FONT);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        return font;
    }

    private XSSFFont getCellFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName(FONT);
        font.setFontHeightInPoints((short) 11);
        return font;
    }

    private CellStyle getHeaderStyle(XSSFWorkbook workbook, Font font) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private CellStyle getCellStyle(XSSFWorkbook workbook, Font font) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }

}
