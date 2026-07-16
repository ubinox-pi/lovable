package com.lovable.service.impls;

import com.lovable.dto.auth.UserDto;
import com.lovable.exception.UserNotFoundException;
import com.lovable.mapper.UserMapper;
import com.lovable.repository.UserRepository;
import com.lovable.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 10-07-2026 16:58
 * File: UserServiceV1Impl
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceV1Impl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getMe(String email) {
        return userMapper.toUserDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email)));
    }
}
