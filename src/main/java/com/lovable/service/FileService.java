package com.lovable.service;

import com.lovable.dto.project.FileContentResponse;
import com.lovable.dto.project.FileTreeResponse;
import com.lovable.entity.User;
import jakarta.validation.constraints.NotNull;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service
 * Created by: Ashish Kushwaha on 11-07-2026 19:48
 * File: FileService
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface FileService {

    FileTreeResponse getFileTree(String email, Long projectId);

    FileContentResponse getFileContent(String email, Long projectId, String path);

    void saveFile(User user, @NotNull(message = "Project ID cannot be null") Long projectId, String filePath, String fileContent);
}
