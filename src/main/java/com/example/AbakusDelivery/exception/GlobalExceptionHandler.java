package com.example.AbakusDelivery.exception;

import com.example.AbakusDelivery.dto.response.ErrorResponse;
import com.example.AbakusDelivery.dto.response.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Ошибки @Valid на @RequestBody (DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldErrorResponse(err.getField(), err.getDefaultMessage()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                "Validation failed",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    // Ошибки валидации параметров (@PathVariable, @RequestParam и т.п.)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new FieldErrorResponse(
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ))
                .toList();

        ErrorResponse body = new ErrorResponse(
                "Validation failed",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    // Бизнес-ошибки (not found, некорректные данные и т.п.)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(body);
    }

    // Все неожиданные ошибки -> 500 + лог со стектрейсом
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        // Логируем полную ошибку
        log.error("Unexpected error on path {}", request.getRequestURI(), ex);

        ErrorResponse body = new ErrorResponse(
                "Internal server error",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                OffsetDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
