package com.example.orders.exception;

import java.time.OffsetDateTime;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiError> api(ApiException e) {
        return response(e.status(), e.code(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalid() {
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request data is invalid");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> malformed() {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request data is malformed");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected() {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<ApiError> response(HttpStatus s, String c, String m) {
        return ResponseEntity.status(s).body(new ApiError(OffsetDateTime.now(), s.value(), c, m));
    }
}
