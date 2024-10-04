package com.demo.usermanagementservice.exception;

public class DuplicatedEmailException extends RuntimeException {

    public DuplicatedEmailException(String message) {
        super(message);
    }
}
