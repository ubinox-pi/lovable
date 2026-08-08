package com.lovable.service.impls;

import com.lovable.dto.chat.ChatRequest;
import com.lovable.entity.User;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.llm.PromptUtils;
import com.lovable.repository.UserRepository;
import com.lovable.service.AIGenerationService;
import com.lovable.service.FileSavingService;
import com.lovable.util.AppUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 06-08-2026 16:03
 * File: AiGenerationServiceV1Impl
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
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceV1Impl implements AIGenerationService {

    private final ChatClient chatClient;
    private final FileSavingService fileSavingService;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("@security.canEditProject(#request.projectId)")
    public Flux<String> streamChat(ChatRequest request) {
        String email = AppUtils.getCurrentUserEmail();
        createChatSessionIfNotExists(request.getProjectId());

        Map<String, Object> advisorParams = Map.of(
                "email", email,
                "projectId", request.getProjectId()
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        User user = userRepository.findById(AppUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(request.getMessage())
                .advisors(advisorSpec -> advisorSpec.params(advisorParams))
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    log.info("Received response: {}", response);
                    if (response.getResult() == null) {
                        log.warn("Received null result in response for projectId: {}", request.getProjectId());
                        return;
                    }
                    fullResponseBuffer.append(response.getResult().getOutput().getText());
                })
                .doOnComplete(() -> {
                    log.info("Chat streaming completed for projectId: {}", request.getProjectId());
                    log.debug("Full response buffer: {}", fullResponseBuffer);
                    fileSavingService.parseAndSaveFile(user, fullResponseBuffer.toString(), request.getProjectId());
                })
                .doOnError(error -> log.error("Error during chat streaming for projectId: {}, error: {}", request.getProjectId(), error.getMessage()))
                .mapNotNull(response -> {
                    if (response.getResult() == null) return null;
                    return response.getResult().getOutput().getText();
                });

    }

    private void createChatSessionIfNotExists(@NotNull(message = "Project ID cannot be null") Long projectId) {
    }
}
