package com.lovable.llm;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.llm
 * Created by: Ashish Kushwaha on 22-08-2026 19:36
 * File: LlmResponseParser
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.entity.ChatEvent;
import com.lovable.entity.ChatMessage;
import com.lovable.enums.ChatEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LlmResponseParser {

    private static final Pattern GENERIC_TAG_PATTERN = Pattern.compile(
            "(<(message|file|tool)([^>]*)>)([\\s\\s]*?)(</\\2>)"
            , Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "(path|args)=\"([^\"]+)\""
    );

    public List<ChatEvent> parseChatEvent(String fullResponse, ChatMessage chatMessage) {
        List<ChatEvent> chatEvents = new ArrayList<>();

        int orderCounter = 1;

        Matcher matcher = GENERIC_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find()) {
            String tag = matcher.group(2).toLowerCase(Locale.ROOT);
            String attributes = matcher.group(3);
            String content = matcher.group(4).trim();

            Map<String, String> attributeMap = extractAttribute(attributes);

            ChatEvent.ChatEventBuilder chatEventBuilder = ChatEvent.builder()
                    .chatMessage(chatMessage)
                    .sequenceOrder(orderCounter++)
                    .chatEventType(null)
                    .content(content)
                    .filePath(null)
                    .metadata(null);

            switch (tag) {
                case "message" -> chatEventBuilder.chatEventType(ChatEventType.MESSAGE);
                case "file" -> {
                    chatEventBuilder.chatEventType(ChatEventType.FILE_EDIT);
                    chatEventBuilder.filePath(attributeMap.get("path"));
                }
                case "tool" -> {
                    chatEventBuilder.chatEventType(ChatEventType.TOOL_LOG);
                    chatEventBuilder.metadata(attributeMap.get("args"));
                }
                default -> {
                    log.warn("Unknown tag: {}", tag);
                    continue;
                }
            }
            chatEvents.add(chatEventBuilder.build());
        }
        return chatEvents;
    }

    private Map<String, String> extractAttribute(String attributes) {
        Map<String, String> attributeMap = new HashMap<>();
        if (attributes == null || attributes.isBlank()) return attributeMap;

        Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributes);
        while (matcher.find()) {
            attributeMap.put(matcher.group(1), matcher.group(2));
        }
        return attributeMap;
    }
}
