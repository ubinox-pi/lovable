package com.lovable.service.impls;

import com.lovable.dto.chat.ChatRequest;
import com.lovable.entity.*;
import com.lovable.enums.ChatEventType;
import com.lovable.enums.MessageRole;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.llm.LlmResponseParser;
import com.lovable.llm.PromptUtils;
import com.lovable.llm.advisors.FileTreeContextAdviser;
import com.lovable.llm.tools.CodeGenerationTools;
import com.lovable.repository.*;
import com.lovable.service.AIGenerationService;
import com.lovable.service.FileSavingService;
import com.lovable.service.FileService;
import com.lovable.util.AppUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
    private final FileService fileService;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final LlmResponseParser llmResponseParser;
    private final ChatEventRepository chatEventRepository;

    @Override
    @PreAuthorize("@security.canEditProject(#request.projectId)")
    public Flux<String> streamChat(ChatRequest request) {
        String email = AppUtils.getCurrentUserEmail();
        ChatSession chatSession = createChatSessionIfNotExists(request.getProjectId());

        Map<String, Object> advisorParams = Map.of(
                "email", email,
                "projectId", request.getProjectId()
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        User user = userRepository.findById(AppUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FileTreeContextAdviser fileTreeContextAdviser = new FileTreeContextAdviser(fileService, user.getEmail());
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(fileService, user.getEmail(), request.getProjectId());

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(request.getMessage())
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdviser);
                })
                .tools(codeGenerationTools)
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    log.info("Received response: {}", response);
                    String content = Objects.requireNonNull(response.getResult()).getOutput().getText();
                    if (response.getResult() == null) {
                        log.warn("Received null result in response for projectId: {}", request.getProjectId());
                        return;
                    }
                    fullResponseBuffer.append(content);
                    if (content != null && !content.isEmpty() && endTime.get() == 0L)
                        endTime.set(System.currentTimeMillis());
                })
                .doOnComplete(() -> {
                    log.info("Chat streaming completed for projectId: {}", request.getProjectId());
                    log.debug("Full response buffer: {}", fullResponseBuffer);
//                    fileSavingService.parseAndSaveFile(user, fullResponseBuffer.toString(), request.getProjectId());
                    long duration = (endTime.get() - startTime.get()) / 1000;
                    finalizeChats(user, request.getMessage(), chatSession, fullResponseBuffer.toString(), duration);
                })
                .doOnError(error -> log.error("Error during chat streaming for projectId: {}, error: {}", request.getProjectId(), error.getMessage()))
                .mapNotNull(response -> {
                    if (response.getResult() == null) return null;
                    return response.getResult().getOutput().getText();
                });

    }

    private ChatSession createChatSessionIfNotExists(@NotNull(message = "Project ID cannot be null") Long projectId) {
        ChatSessionId chatSessionId = new ChatSessionId(AppUtils.getCurrentUserId(), projectId);
        AtomicReference<ChatSession> chatSession = new AtomicReference<>();
        chatSessionRepository.findById(chatSessionId)
                .ifPresentOrElse(chatSession::set, () -> {
                            Project project = projectRepository.findById(projectId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
                            User user = userRepository.getReferenceById(AppUtils.getCurrentUserId());

                            chatSession.set(ChatSession.builder()
                                    .id(chatSessionId)
                                    .project(project)
                                    .user(user)
                                    .build());

                            chatSession.set(chatSessionRepository.save(chatSession.get()));
                        }
                );
        return chatSession.get();
    }

    private void finalizeChats(User user, String userMessage, ChatSession chatSession, String fullText, long duration) {

        Long projectId = chatSession.getProject().getId();

        chatMessageRepository.save(ChatMessage.builder()
                .chatSession(chatSession)
                .content(userMessage)
                .role(MessageRole.USER)
                .build());

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .chatSession(chatSession)
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here...")
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvent(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                .chatEventType(ChatEventType.THOUGHT)
                .chatMessage(assistantChatMessage)
                .content("Thought for " + duration + "s")
                .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getChatEventType() == ChatEventType.FILE_EDIT)
                .forEach(e -> fileSavingService.parseAndSaveFile(user, e.getContent(), projectId));

        chatEventRepository.saveAll(chatEventList);
    }
}
