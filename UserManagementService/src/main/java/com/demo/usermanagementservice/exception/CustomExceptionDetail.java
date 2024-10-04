package com.demo.usermanagementservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class CustomExceptionDetail {
    private LocalDateTime timestamp;
    private String message;
    private String requestURI;
    private String errorCode;

}
