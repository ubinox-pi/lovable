package com.lovable.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.exception
 * Created by: Ashish Kushwaha on 20-07-2026 23:55
 * File: ErrorCode
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
@Getter
public enum ErrorCode {

    SUCCESS(
            HttpStatus.OK,
            "SUCCESS",
            "Request completed successfully"
    ),

    BAD_REQUEST(
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST",
            "Bad request"
    ),

    VALIDATION_FAILED(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_FAILED",
            "Validation failed"
    ),

    INVALID_JSON(
            HttpStatus.BAD_REQUEST,
            "INVALID_JSON",
            "Malformed JSON request"
    ),

    INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "INVALID_PARAMETER",
            "Invalid request parameter"
    ),

    MISSING_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "MISSING_PARAMETER",
            "Required request parameter is missing"
    ),

    MISSING_HEADER(
            HttpStatus.BAD_REQUEST,
            "MISSING_HEADER",
            "Required request header is missing"
    ),

    TYPE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "TYPE_MISMATCH",
            "Parameter type mismatch"
    ),

    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED",
            "Authentication required"
    ),

    INVALID_SESSION(
            HttpStatus.UNAUTHORIZED,
            "INVALID_SESSION",
            "Invalid session"
    ),

    SESSION_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            "SESSION_EXPIRED",
            "Session expired"
    ),

    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "Access denied"
    ),

    RESOURCE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            "Resource not found"
    ),

    ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "ALREADY_EXISTS",
            "Resource already exists"
    ),

    CONFLICT(
            HttpStatus.CONFLICT,
            "CONFLICT",
            "Request conflict"
    ),

    DATABASE_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "DATABASE_ERROR",
            "Database error"
    ),

    DATABASE_LOCK_ERROR(
            HttpStatus.CONFLICT,
            "DATABASE_LOCK_ERROR",
            "Database lock error"
    ),

    DATA_INTEGRITY(
            HttpStatus.CONFLICT,
            "DATA_INTEGRITY",
            "Database integrity violation"
    ),

    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "METHOD_NOT_ALLOWED",
            "HTTP method not supported"
    ),

    UNSUPPORTED_MEDIA_TYPE(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "UNSUPPORTED_MEDIA_TYPE",
            "Unsupported media type"
    ),

    FILE_UPLOAD_ERROR(
            HttpStatus.BAD_REQUEST,
            "FILE_UPLOAD_ERROR",
            "File upload failed"
    ),

    FILE_TOO_LARGE(
            HttpStatus.PAYLOAD_TOO_LARGE,
            "FILE_TOO_LARGE",
            "Uploaded file is too large"
    ),

    RATE_LIMIT_EXCEEDED(
            HttpStatus.TOO_MANY_REQUESTS,
            "RATE_LIMIT_EXCEEDED",
            "Too many requests"
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "Internal server error"
    ),

    SERVICE_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "SERVICE_UNAVAILABLE",
            "Service temporarily unavailable"
    ), UNSUPPORTED_MEDIA(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA", "Unsupported media type");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status,
              String code,
              String defaultMessage) {

        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
