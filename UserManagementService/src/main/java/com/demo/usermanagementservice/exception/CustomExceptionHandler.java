package com.demo.usermanagementservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest webRequest) {
        final CustomExceptionDetail customExceptionDetail =
                new CustomExceptionDetail(LocalDateTime.now(),
                        ex.getMessage(),
                        webRequest.getDescription(false),
                        CustomErrorCode.GENERAL.code);

        return ResponseEntity.internalServerError().body(customExceptionDetail);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest webRequest) {
        final CustomExceptionDetail customExceptionDetail =
                new CustomExceptionDetail(LocalDateTime.now(),
                        ex.getMessage(),
                        webRequest.getDescription(false),
                        CustomErrorCode.USER_NOT_FOUND.code);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customExceptionDetail);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest webRequest) {
        final CustomExceptionDetail customExceptionDetail =
                new CustomExceptionDetail(LocalDateTime.now(),
                        ex.getMessage(),
                        webRequest.getDescription(false),
                        CustomErrorCode.BAD_REQUEST.code);

        return ResponseEntity.badRequest().body(customExceptionDetail);
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public final ResponseEntity<Object> handleDuplicatedEmailException(DuplicatedEmailException ex, WebRequest webRequest) {
        final CustomExceptionDetail customExceptionDetail =
                new CustomExceptionDetail(LocalDateTime.now(),
                        ex.getMessage(),
                        webRequest.getDescription(false),
                        CustomErrorCode.USER_ALREADY_EXIST.code);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(customExceptionDetail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        final StringBuilder errorMessage = new StringBuilder("Errors: ");
        ex.getFieldErrors().forEach(fieldError -> errorMessage.append(fieldError.getDefaultMessage()).append(", "));

        final CustomExceptionDetail customExceptionDetail =
                new CustomExceptionDetail(LocalDateTime.now(),
                        errorMessage.substring(0, errorMessage.lastIndexOf(",")),
                        request.getDescription(false),
                        CustomErrorCode.BAD_REQUEST.code);

        return ResponseEntity.badRequest().body(customExceptionDetail);
    }
}
