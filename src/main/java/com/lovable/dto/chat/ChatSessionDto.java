package com.lovable.dto.chat;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.dto.chat
 * Created by: Ashish Kushwaha on 29-08-2026 18:49
 * File: ChatSessionDto
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.auth.UserDto;
import com.lovable.dto.project.ProjectDto;
import com.lovable.entity.ChatSessionId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatSessionDto {
    private ChatSessionId id;

    private ProjectDto project;

    private UserDto user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
