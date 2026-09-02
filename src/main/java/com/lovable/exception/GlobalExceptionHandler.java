package com.lovable.exception;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.exception
 * Created by: Ashish Kushwaha on 20-07-2026 23:28
 * File: GlobalExceptionHandler
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.razorpay.RazorpayException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(
            ApiException ex,
            HttpServletRequest request) {

        log.warn("{} : {}", ex.getErrorCode(), ex.getMessage());

        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ex.getErrorCode(),
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        log.warn("ResponseStatusException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(
                ErrorResponseFactory.build(
                        ErrorCode.TOO_MANY_REQUESTS,
                        ex.getMessage(),
                        request
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        log.warn("Validation failed : {}", errors);

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.VALIDATION_FAILED,
                        "Validation failed",
                        errors,
                        request
                )
        );
    }

    @ExceptionHandler(RazorpayException.class)
    public ResponseEntity<ApiResponse<Void>> handleRazorpayException(
            RazorpayException ex,
            HttpServletRequest request) {

        log.error("Razorpay API error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.PAYMENT_PROVIDER_ERROR,
                                "Payment provider error: " + ex.getMessage(),
                                null,
                                request
                        )
                );
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleRazorpayException(
            WebClientResponseException ex,
            HttpServletRequest request) {

        log.error("Razorpay API returned {}: {}",
                ex.getStatusCode(),
                ex.getResponseBodyAsString(),
                ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.PAYMENT_PROVIDER_ERROR,
                                "Payment provider error",
                                Map.of("responseBody", ex.getResponseBodyAsString()),
                                request
                        )
                );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleHandlerValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach(result ->
                result.getResolvableErrors().forEach(error ->
                        errors.putIfAbsent(
                                result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage()
                        )
                )
        );

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.VALIDATION_FAILED,
                        "Validation failed",
                        errors,
                        request
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
        }

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.VALIDATION_FAILED,
                        "Validation failed",
                        errors,
                        request
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Database constraint violation", ex);

        return ResponseEntity
                .status(ErrorCode.DATA_INTEGRITY.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.DATA_INTEGRITY,
                                "Database constraint violation",
                                request
                        )
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied");

        return ResponseEntity
                .status(ErrorCode.ACCESS_DENIED.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.ACCESS_DENIED,
                                "You don't have permission to access this resource. " + ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleErrorResponse(
            ErrorResponseException ex,
            HttpServletRequest request) {

        log.warn(ex.getMessage());

        String message = ex.getBody().getDetail();

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.BAD_REQUEST,
                                message,
                                request
                        )
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed JSON request", ex);

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.BAD_REQUEST,
                        "Malformed JSON request.",
                        request
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.BAD_REQUEST,
                        "Missing request parameter: " + ex.getParameterName(),
                        request
                )
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingHeader(
            MissingRequestHeaderException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.BAD_REQUEST,
                        "Missing request header: " + ex.getHeaderName(),
                        request
                )
        );
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVariable(
            MissingPathVariableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.BAD_REQUEST,
                        "Missing path variable: " + ex.getVariableName(),
                        request
                )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ErrorResponseFactory.build(
                        ErrorCode.BAD_REQUEST,
                        "Invalid value for parameter '" + ex.getName() + "'",
                        request
                )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.METHOD_NOT_ALLOWED,
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.UNSUPPORTED_MEDIA,
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "Requested resource not found.\n" + ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKey(
            DuplicateKeyException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.ALREADY_EXISTS.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.ALREADY_EXISTS,
                                "Duplicate resource.\n" + ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(
            OptimisticLockException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.CONFLICT.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.CONFLICT,
                                "The resource was modified by another transaction.\n" + ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccess(
            DataAccessException ex,
            HttpServletRequest request) {

        log.error("Database error", ex);

        return ResponseEntity.internalServerError()
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.INTERNAL_SERVER_ERROR,
                                "Database error.",
                                request
                        )
                );
    }

    @ExceptionHandler({
            CannotAcquireLockException.class,
            PessimisticLockingFailureException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleLockingFailure(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.CONFLICT.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.CONFLICT,
                                "Database locking failure.\n" + ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());

        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.UNAUTHORIZED,
                                "Authentication failed.",
                                request
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn(ex.getMessage());

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.BAD_REQUEST,
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        log.error(ex.getMessage(), ex);

        return ResponseEntity.internalServerError()
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.INTERNAL_SERVER_ERROR,
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedOperation(
            UnsupportedOperationException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus())
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.BAD_REQUEST,
                                ex.getMessage(),
                                request
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception", ex);

        return ResponseEntity.internalServerError()
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred.",
                                request
                        )
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        log.error("Unhandled runtime exception", ex);

        return ResponseEntity.internalServerError()
                .body(
                        ErrorResponseFactory.build(
                                ErrorCode.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred.",
                                request
                        )
                );
    }
}
