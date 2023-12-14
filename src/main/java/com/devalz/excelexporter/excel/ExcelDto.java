package com.devalz.excelexporter.excel;

public class ExcelDto {

    private String fileName;
    private byte[] content;

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    private ExcelDto(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }

    public static class Builder {

        private String fileName;
        private byte[] content;

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder content(byte[] content) {
            this.content = content;
            return this;
        }

        public ExcelDto build() {
            return new ExcelDto(this.fileName, this.content);
        }

    }
}
