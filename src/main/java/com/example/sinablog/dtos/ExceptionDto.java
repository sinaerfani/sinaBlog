package com.example.sinablog.dtos;

public class ExceptionDto {

    private final String title;
    private final String code;

    public ExceptionDto(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }
}
