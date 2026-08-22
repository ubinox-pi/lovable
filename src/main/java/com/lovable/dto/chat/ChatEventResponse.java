package com.lovable.dto.chat;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.dto.chat
 * Created by: Ashish Kushwaha on 22-08-2026 12:06
 * File: ChatEventResponse
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.enums.ChatEventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatEventResponse {

    private Long id;

    private Integer sequenceOrder;

    private ChatEventType chatEventType;

    private String content;

    private String filePath;

    private String metadata;
}
