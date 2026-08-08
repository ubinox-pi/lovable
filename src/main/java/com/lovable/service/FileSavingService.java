package com.lovable.service;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service
 * Created by: Ashish Kushwaha on 08-08-2026 17:39
 * File: FileSavingService
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Async("fileSavingExecutor")
@Service
@RequiredArgsConstructor
public class FileSavingService {
    private static final Pattern FILE_TAG_PATTERN =
            Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);
    private final FileService fileService;

    public void parseAndSaveFile(User user, String fullResponse, @NotNull(message = "Project ID cannot be null") Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        log.debug("Starting to parse and save files for projectId: {}", projectId);
        log.debug("Full response: {}", fullResponse);
        log.debug("all matches: {}", matcher);

        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            log.debug("Found file to save: {} for projectId: {}", filePath, projectId);
            log.debug("File content: {}", fileContent);
            fileService.saveFile(user, projectId, filePath, fileContent);
        }
    }

}
