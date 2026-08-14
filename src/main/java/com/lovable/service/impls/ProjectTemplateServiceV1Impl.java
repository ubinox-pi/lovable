package com.lovable.service.impls;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 11-08-2026 18:04
 * File: ProjectTemplateServiceV1Impl
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.entity.Project;
import com.lovable.entity.ProjectFile;
import com.lovable.entity.User;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.repository.ProjectFileRepository;
import com.lovable.repository.ProjectRepository;
import com.lovable.service.ProjectTemplateService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Async("minioTemplateCopyExecutor")
public class ProjectTemplateServiceV1Impl implements ProjectTemplateService {
    private static final String TEMPLATE_BUCKET = "starterproject";
    private static final String TARGET_BUCKET = "lovablebucket";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter";

    private final MinioClient minioClient;
    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;

    @Override
    @Transactional
    public void initializeProjectFromTemplate(User user, Long projectId, String templateName) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>();

            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String targetKey = projectId + "/" + cleanPath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(targetKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                ProjectFile projectFile = ProjectFile.builder()
                        .project(project)
                        .createdBy(user)
                        .path(cleanPath)
                        .objectKey(targetKey)
                        .build();

                filesToSave.add(projectFile);
            }
            projectFileRepository.saveAll(filesToSave);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }
}
