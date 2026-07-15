package com.lovable.service.impls;

import com.lovable.dto.project.FileContentResponse;
import com.lovable.dto.project.FileTreeResponse;
import com.lovable.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 11-07-2026 20:15
 * File: FileServiceImplV1
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
public class FileServiceImplV1 implements FileService {
    @Override
    public FileTreeResponse getFileTree(String email, Long projectId) {
        return null;
    }

    @Override
    public FileContentResponse getFileContent(String email, Long projectId, String path) {
        return null;
    }
}
