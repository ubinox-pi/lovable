package com.lovable.service.impls;

import com.lovable.dto.project.ProjectDto;
import com.lovable.entity.Project;
import com.lovable.entity.User;
import com.lovable.exception.ProjectNotFoundException;
import com.lovable.exception.UserNotFoundException;
import com.lovable.mapper.ProjectMapper;
import com.lovable.repository.ProjectRepository;
import com.lovable.repository.UserRepository;
import com.lovable.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 10-07-2026 17:10
 * File: ProjectServiceV1Impl
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
public class ProjectServiceV1Impl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectDto> getUserAllProject(String email) {
        log.info("Getting all projects for user with email: {}", email);
        return projectRepository.findAllAccessibleProjectsByUser(email)
                .stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    @Override
    public ProjectDto getUserProjectById(String email, Long projectId) {
        log.info("Getting project with id: {} for user with email: {}", projectId, email);
        return projectMapper.toProjectDto(getAccessibleProject(email, projectId));
    }

    @Override
    public ProjectDto createProject(String email, ProjectDto projectDto) {
        log.info("Creating project for user with email: {} and project {}", email, projectDto.getName());
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email " + email + " not found")
        );

        Project project = Project.builder()
                .name(projectDto.getName())
                .owner(user)
                .build();
        project = projectRepository.save(project);
        return projectMapper.toProjectDto(project);
    }

    @Override
    public ProjectDto updateProject(String email, Long projectId, ProjectDto projectDto) {
        log.info("Updating project with id: {} for user with email: {}", projectId, email);
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new AccessDeniedException("Only owner can update project");
        }

        project.setName(projectDto.getName());
        project = projectRepository.save(project);
        return projectMapper.toProjectDto(project);
    }

    @Override
    public Void deleteProject(String email, Long projectId) {
        log.info("Deleting project with id: {} for user with email: {}", projectId, email);
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new AccessDeniedException("Only owner can delete project");
        }

        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
        return null;
    }

    private Project getAccessibleProject(String email, Long projectId) {
        return projectRepository.findAccessibleProjectById(email, projectId)
                .orElseThrow(
                        () -> new ProjectNotFoundException(
                                "Project with id " + projectId + " not found for user with email " + email
                        ));
    }
}
