package com.gmail.voronovskyi.yaroslav.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<ApiError> handlerRequestException(EntityNotFoundException exception) {
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, exception, ErrorKey.ENTITY_NOT_FOUND);
    }

    @ExceptionHandler(value = {EmptyResultDataAccessException.class})
    public ResponseEntity<ApiError> handlerRequestException(EmptyResultDataAccessException exception) {
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, exception, ErrorKey.ENTITY_NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ApiError> handlerRequestException(InvalidDataAccessApiUsageException exception) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, exception, ErrorKey.INVALID_DATA);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ApiError> handlerRequestException(IllegalArgumentException exception) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, exception, ErrorKey.INVALID_DATA);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handlerRequestException(MethodArgumentNotValidException exception) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, exception, ErrorKey.INVALID_DATA);
    }

    @ExceptionHandler(value = {BankClientException.class})
    public ResponseEntity<ApiError> handlerRequestException(BankClientException exception) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, exception, ErrorKey.INVALID_DATA);
    }

    @ExceptionHandler(value = {PaymentProcessingException.class})
    public ResponseEntity<ApiError> handlerRequestException(PaymentProcessingException exception) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, exception, ErrorKey.INVALID_DATA);
    }

    @ExceptionHandler(value = {PaymentNotFoundException.class})
    public ResponseEntity<ApiError> handlerRequestException(PaymentNotFoundException exception) {
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, exception, ErrorKey.ENTITY_NOT_FOUND);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handlerRequestException(DataIntegrityViolationException exception) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, exception, ErrorKey.DUPLICATE_EXTERNAL_ID);
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<ApiError> handlerRequestException(NoResourceFoundException exception) {
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, exception, ErrorKey.RESOURCE_NOT_FOUND);
    }

    private ResponseEntity<ApiError> buildErrorResponseEntity(HttpStatus status, Exception exception, ErrorKey key) {
        log.error("API Error", exception);
        ApiError apiError = new ApiError(status, exception, null, key);
        return ResponseEntity.status(status).body(apiError);
    }
}
