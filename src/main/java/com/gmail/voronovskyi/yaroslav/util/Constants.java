package com.gmail.voronovskyi.yaroslav.util;

import java.time.Duration;

public class Constants {

    public static final String ERROR_STATUS = "error";
    public static final String CODE_400 = "400";
    public static final String HTTP_STATUS_BAD_REQUEST = "Bad Request";
    public static final String CODE_404 = "404";
    public static final String HTTP_STATUS_NOT_FOUND = "Not Found";

    public static final Duration BANK_DELAY = Duration.ofMillis(300);

    private Constants() {
    }
}
