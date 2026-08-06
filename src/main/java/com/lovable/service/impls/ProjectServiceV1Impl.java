package com.lovable.service.impls;

import com.lovable.dto.project.ProjectDto;
import com.lovable.entity.Project;
import com.lovable.entity.ProjectMember;
import com.lovable.entity.ProjectMemberId;
import com.lovable.entity.User;
import com.lovable.enums.ProjectRole;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.exception.custom.UnauthorizedException;
import com.lovable.mapper.ProjectMapper;
import com.lovable.repository.ProjectMemberRepository;
import com.lovable.repository.ProjectRepository;
import com.lovable.repository.UserRepository;
import com.lovable.service.ProjectService;
import com.lovable.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final SubscriptionService subscriptionService;

    @Override
    public List<ProjectDto> getUserAllProject(String email) {
        log.info("Getting all projects for user with email: {}", email);
        return projectRepository.findAllAccessibleProjectsByUser(email)
                .stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectDto getUserProjectById(String email, Long projectId) {
        log.info("Getting project with id: {} for user with email: {}", projectId, email);
        return projectMapper.toProjectDto(getAccessibleProject(email, projectId));
    }

    @Override
    public ProjectDto createProject(String email, ProjectDto projectDto) {
        log.info("Creating project for owner with email: {} and project {}", email, projectDto.getName());
        User owner = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User with email " + email + " not found")
        );

        if (subscriptionService.canCreateNewProject()) {
            log.info("User with email: {} has an active subscription and can create a new project", email);
        } else {
            log.warn("User with email: {} does not have an active subscription and cannot create a new project", email);
            throw new UnauthorizedException("User with email " + email + " does not have an active subscription and cannot create a new project");
        }

        Project project = Project.builder()
                .name(projectDto.getName())
                .owner(owner)
                .build();

        project = projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder()
                .id(new ProjectMemberId(project.getId(), owner.getId()))
                .project(project)
                .user(owner)
                .projectRole(ProjectRole.OWNER)
                .invitedAt(LocalDateTime.now())
                .build();

        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectDto(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectDto updateProject(String email, Long projectId, ProjectDto projectDto) {
        log.info("Updating project with id: {} for user with email: {}", projectId, email);
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("Only owner can update project");
        }

        project.setName(projectDto.getName());
        project = projectRepository.save(project);
        return projectMapper.toProjectDto(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Void deleteProject(String email, Long projectId) {
        log.info("Deleting project with id: {} for user with email: {}", projectId, email);
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("Only owner can delete project");
        }

        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
        return null;
    }

    private Project getAccessibleProject(String email, Long projectId) {
        return projectRepository.findAccessibleProjectById(email, projectId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project with id " + projectId + " not found for user with email " + email
                        ));
    }
}
