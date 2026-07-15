package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 10-07-2026 14:57
 * File: AuthController
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.auth.LoginRequest;
import com.lovable.dto.auth.UserDto;
import com.lovable.service.AuthService;
import com.lovable.service.UserService;
import com.lovable.util.AppUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signupRequest(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(authService.signup(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginRequest(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        String email = AppUtils.getUserEmail();
        return new ResponseEntity<>(userService.getMe(email), HttpStatus.CREATED);
    }
}
