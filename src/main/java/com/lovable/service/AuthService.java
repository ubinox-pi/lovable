package com.lovable.service;

import com.lovable.dto.auth.LoginRequest;
import com.lovable.dto.auth.UserDto;
import jakarta.validation.Valid;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service
 * Created by: Ashish Kushwaha on 10-07-2026 14:58
 * File: AuthServiceImplV1
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface AuthService {
    UserDto signup(@Valid UserDto userDto);

    Void login(@Valid LoginRequest loginRequest);
}
