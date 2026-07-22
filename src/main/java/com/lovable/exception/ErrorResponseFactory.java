package com.lovable.exception;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Map;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.exception
 * Created by: Ashish Kushwaha on 21-07-2026 00:10
 * File: ErrorResponseFactory
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public final class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    public static ApiResponse<Void> build(
            ErrorCode errorCode,
            String message,
            HttpServletRequest request) {

        return ApiResponse.<Void>builder()
                .success(false)
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> build(
            ErrorCode errorCode,
            String message,
            Map<String, String> validationErrors,
            HttpServletRequest request) {

        return ApiResponse.<Void>builder()
                .success(false)
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(message)
                .validationErrors(validationErrors)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }
}
