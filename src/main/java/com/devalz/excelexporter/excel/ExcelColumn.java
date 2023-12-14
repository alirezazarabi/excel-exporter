package com.devalz.excelexporter.excel;

import com.devalz.excelexporter.utils.ResourceBundleUtils;
import org.springframework.util.Assert;

import java.util.function.Consumer;
import java.util.function.Function;

public class ExcelColumn<T> {

    private String headerName;
    private Function<T, ?> source;
    private Type type;
    private String dataFormat;

    private ExcelColumn() {

    }

    public String getHeaderName() {
        return headerName;
    }

    public Function<T, ?> getSource() {
        return source;
    }

    public Type getType() {
        return type;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public static class Builder<T> {

        private String headerName;
        private Function<T, ?> source;
        private Type type = Type.STRING;
        private String dataFormat;

        private final ExcelExporter.Builder<T> parentBuilder;
        private final Consumer<ExcelColumn<T>> endOperation;

        public Builder(ExcelExporter.Builder<T> parentBuilder,
                       Consumer<ExcelColumn<T>> endOperation) {
            this.parentBuilder = parentBuilder;
            this.endOperation = endOperation;
        }

        public Builder<T> headerName(String headerName) {
            this.headerName = ResourceBundleUtils.getMessage(headerName);
            return this;
        }

        public Builder<T> source(Function<T, ?> source) {
            this.source = source;
            return this;
        }

        public Builder<T> type(Type type) {
            this.type = type;
            return this;
        }

        public Builder<T> dataFormat(String dataFormat) {
            this.dataFormat = dataFormat;
            return this;
        }

        public Builder<T> thousandSeparatorFormat() {
            this.dataFormat = DataFormat.THOUSAND_SEPARATOR.getCode();
            return this;
        }

        public ExcelExporter.Builder<T> end() {
            endOperation.accept(this.build());
            return parentBuilder;
        }

        public ExcelColumn<T> build() {
            validateProperty();
            ExcelColumn<T> excelColumn = new ExcelColumn<>();
            excelColumn.headerName = this.headerName;
            excelColumn.source = this.source;
            excelColumn.type = this.type;
            excelColumn.dataFormat = this.dataFormat;
            return excelColumn;
        }

        private void validateProperty() {
            Assert.notNull(this.headerName, "headerName is null");
            Assert.notNull(this.source, "source is null");
            Assert.notNull(this.type, "type is null");
        }
    }

    public enum Type {
        NUMERIC,
        STRING,
        BOOLEAN
    }

    public enum DataFormat {
        THOUSAND_SEPARATOR("#,###");

        private final String code;

        DataFormat(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
