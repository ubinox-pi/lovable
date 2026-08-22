package com.lovable.llm.advisors;

import com.lovable.dto.project.FileTreeResponse;
import com.lovable.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.llm.advisors
 * Created by: Ashish Kushwaha on 11-08-2026 16:27
 * File: FileTreeContextAdviser
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@Slf4j
@RequiredArgsConstructor
public class FileTreeContextAdviser implements StreamAdvisor {

    private final FileService fileService;
    private final String email;

    @Override
    public @NonNull Flux<ChatClientResponse> adviseStream(
            @NonNull ChatClientRequest chatClientRequest,
            @NonNull StreamAdvisorChain streamAdvisorChain
    ) {
        Map<String, Object> context = chatClientRequest.context();
        Long projectId = Long.valueOf(context.get("projectId").toString());

        ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(chatClientRequest, projectId);

        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    private ChatClientRequest augmentRequestWithFileTree(
            ChatClientRequest request,
            Long projectId
    ) {
        List<Message> messages =
                new ArrayList<>(request.prompt().getInstructions());

        boolean fileTreeAlreadyPresent = messages.stream()
                .filter(message -> message.getMessageType() == MessageType.SYSTEM)
                .map(Message::getText)
                .anyMatch(text -> text.contains("----FILE TREE----"));

        if (fileTreeAlreadyPresent) {
            return request;
        }

        FileTreeResponse fileTree =
                fileService.getFileTree(email, projectId);

        String fileTreeContext = """
                ----FILE TREE----
                %s
                """.formatted(fileTree);

        int insertIndex = 0;

        while (insertIndex < messages.size()
                && messages.get(insertIndex).getMessageType() == MessageType.SYSTEM) {
            insertIndex++;
        }

        messages.add(insertIndex, new SystemMessage(fileTreeContext));

        return request
                .mutate()
                .prompt(new Prompt(messages, request.prompt().getOptions()))
                .build();
    }

    /**
     * Return the name of the advisor.
     *
     * @return the advisor name.
     */
    @Override
    public @NonNull String getName() {
        return "fileTreeContextAdvisor";
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
