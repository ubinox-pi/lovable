package com.lovable.service.impls;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 22-08-2026 11:56
 * File: ChatServiceImpl
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.chat.ChatMessageResponse;
import com.lovable.entity.ChatMessage;
import com.lovable.entity.ChatSession;
import com.lovable.entity.ChatSessionId;
import com.lovable.mapper.ChatMessageMapper;
import com.lovable.repository.ChatMessageRepository;
import com.lovable.repository.ChatSessionRepository;
import com.lovable.service.ChatService;
import com.lovable.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatMessageResponse> getProjectChatHistory(Long projectId) {
        Long userId = AppUtils.getCurrentUserId();
        String email = AppUtils.getCurrentUserEmail();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(userId, projectId)
        );

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);

        log.info("Fetching chat history for projectId: {} and user: {}", projectId, email);
        return chatMessageList.stream()
                .map(chatMessageMapper::toChatResponse)
                .toList();
    }
}
