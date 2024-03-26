package com.bioproj.utils;

public enum Base64FileTypeEnum {
    // 文件类型
    BASE64_FILETYPE_DOC("doc", "data:application/msword;base64,"),
    BASE64_FILETYPE_DOCX("docx", "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,"),
    BASE64_FILETYPE_XLS("xls", "data:application/vnd.ms-excel;base64,"),
    BASE64_FILETYPE_XLSX("xlsx", "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,"),
    BASE64_FILETYPE_PDF("pdf", "data:application/pdf;base64,"),
    BASE64_FILETYPE_PPT("ppt", "data:application/vnd.ms-powerpoint;base64,"),
    BASE64_FILETYPE_PPTX("pptx", "data:application/vnd.openxmlformats-officedocument.presentationml.presentation;base64,"),
    BASE64_FILETYPE_TXT(".txt", "data:text/plain;base64,"),

    // 图片类型
    BASE64_FILETYPE_PNG("png", "data:image/png;base64,"),
    BASE64_FILETYPE_JPG("jpg", "data:image/jpeg;base64,"),
    BASE64_FILETYPE_JPEG("jpeg", "data:image/jpeg;base64,"),
    BASE64_FILETYPE_GIF("gif", "data:image/gif;base64,"),
    BASE64_FILETYPE_SVG("svg", "data:image/svg+xml;base64,"),
    BASE64_FILETYPE_ICO("ico", "data:image/x-icon;base64,"),
    BASE64_FILETYPE_BMP("bmp", "data:image/bmp;base64,"),

    // 二进制流
    BASE64_FILETYPE_OCTET_STREAM("octet-stream", "data:application/octet-stream;base64,"),
    ;

    private Base64FileTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    private String code;
    private String value;

    public String getCode() {return code;}
    public String getValue() {return value;}

    public static String value(String code) {
        Base64FileTypeEnum[] types = values();
        for (Base64FileTypeEnum x : types) {
            if (x.getCode().equals(code)) {
                return x.getValue();
            }
        }
        return null;
    }

}
