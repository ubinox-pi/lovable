package com.lovable.repository;

import com.lovable.entity.ChatSession;
import com.lovable.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.repository
 * Created by: Ashish Kushwaha on 22-08-2026 11:58
 * File: ChatSessionRepository
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
