package com.info.api;

public class APIStatusResponse {
    private int status;
    private String message;
    private String title;

    public APIStatusResponse() {
    }

    public int status() {
        return status;
    }

    public String message() {
        return message;
    }

    public String title() {
        return title;
    }
}
