package com.demo.usermanagementservice.exception;

public enum CustomErrorCode {
    GENERAL("500"),
    BAD_REQUEST("400"),
    USER_ALREADY_EXIST("409_UAE"),
    USER_NOT_FOUND("404_UNF");

    public final String code;

    CustomErrorCode(String code) {
        this.code = code;
    }

}
