package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 06-08-2026 16:01
 * File: ChatController
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
import com.lovable.dto.chat.ChatRequest;
import com.lovable.service.AIGenerationService;
import com.lovable.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat")
public class ChatController {

    private final AIGenerationService aiGenerationService;
    private final ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<String>>> streamChat(@RequestBody @Valid ChatRequest request) {

        return new ResponseEntity<>(aiGenerationService.streamChat(request)
                .map(data -> ServerSentEvent.<String>builder()
                        .data(data)
                        .build()), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/history")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable Long projectId) {
        return new ResponseEntity<>(chatService.getProjectChatHistory(projectId), HttpStatus.OK);
    }
}
