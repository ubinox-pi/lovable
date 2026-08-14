package com.lovable.service.impls;

import com.lovable.config.MinioProperty;
import com.lovable.dto.project.FileContentResponse;
import com.lovable.dto.project.FileTreeResponse;
import com.lovable.entity.ProjectFile;
import com.lovable.entity.User;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.mapper.ProjectFileMapper;
import com.lovable.repository.ProjectFileRepository;
import com.lovable.repository.ProjectRepository;
import com.lovable.service.FileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 11-07-2026 20:15
 * File: FileServiceV1Impl
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
public class FileServiceV1Impl implements FileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final MinioProperty minioProperty;
    private final ProjectFileMapper projectFileMapper;

    @Override
    public FileTreeResponse getFileTree(String email, Long projectId) {
        List<ProjectFile> projectFileList = projectFileRepository.findByProjectIdAndCreatedByEmail(projectId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + projectId + " not found"));
        return FileTreeResponse.builder()
                .files(projectFileMapper.toFileTreeResponse(projectFileList))
                .build();
    }

    @Override
    @PreAuthorize("@security.canEditProject(#email, #projectId)")
    public FileContentResponse getFileContent(String email, Long projectId, String path) {
        String foundPath = projectFileRepository.findByProjectIdAndPath(projectId, path)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with path: " + path + "for project id: " + projectId))
                .getPath();

        String objectKey = projectId + "/" + foundPath;

        try (
                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(minioProperty.getBucketName())
                                .object(objectKey)
                                .build())
        ) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return FileContentResponse.builder()
                    .path(foundPath)
                    .content(content)
                    .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException("Error fetching file content: " + e.getMessage() + ":" + e.getCause() + " for path: " + path + " for project id: " + projectId);
        }
    }

    @Override
    @Transactional
    public void saveFile(User user, Long projectId, String filePath, String fileContent) {
        log.info("Saving file with path: {} for project with id: {}", filePath, projectId);

        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + projectId + " not found"));

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentsByte = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentsByte);
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(minioProperty.getBucketName())
                            .object(objectKey)
                            .stream(inputStream, contentsByte.length, -1)
                            .contentType(determineTheContentType(filePath))
                            .build()
            );

            ProjectFile projectFile = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(projectRepository.findById(projectId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Project with id " + projectId + " not found")))
                            .createdBy(user)
                            .path(cleanPath)
                            .objectKey(objectKey)
                            .build());

            projectFile.setUpdatedAt(LocalDateTime.now());
            projectFileRepository.save(projectFile);

            log.info("File saved successfully {}", objectKey);

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    private String determineTheContentType(String filePath) {
        String type = URLConnection.guessContentTypeFromName(filePath);

        if (type != null) return type;

        if (filePath.endsWith(".jsx") || filePath.endsWith(".tsx") || filePath.endsWith(".ts"))
            return "text/javascript";
        if (filePath.endsWith(".json")) return "application/json";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".yml") || filePath.endsWith(".yaml")) return "text/yaml";
        if (filePath.endsWith(".iml")) return "application/xml";
        if (filePath.endsWith(".md")) return "text/markdown";
        if (filePath.endsWith(".gitignore")) return "text/plain";
        if (filePath.endsWith(".prettierignore")) return "text/plain";
        if (filePath.endsWith(".prettierrc")) return "text/plain";
        return "text/plain";
    }
}
