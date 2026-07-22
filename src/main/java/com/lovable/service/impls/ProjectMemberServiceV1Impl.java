package com.lovable.service.impls;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 15-07-2026 18:50
 * File: ProjectMemberServiceV1Impl
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.member.InviteMemberRequest;
import com.lovable.dto.member.MemberResponse;
import com.lovable.dto.member.UpdateMemberRoleRequest;
import com.lovable.entity.Project;
import com.lovable.entity.ProjectMember;
import com.lovable.entity.ProjectMemberId;
import com.lovable.entity.User;
import com.lovable.exception.custom.AlreadyExistsException;
import com.lovable.exception.custom.BadRequestException;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.exception.custom.UnauthorizedException;
import com.lovable.mapper.ProjectMemberMapper;
import com.lovable.repository.ProjectMemberRepository;
import com.lovable.repository.ProjectRepository;
import com.lovable.repository.UserRepository;
import com.lovable.service.NotificationService;
import com.lovable.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectMemberServiceV1Impl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;
    private final NotificationService emailNotificationService;

    @Override
    public List<MemberResponse> getProjectMembers(String email, Long projectId) {
        return projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toMemberResponse)
                .toList();
    }

    @Override
    public MemberResponse inviteMember(String email, Long projectId, InviteMemberRequest inviteMemberRequest) {
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("Only owner can invite member");
        }

        User invitee = userRepository.findByUniqueUsername(inviteMemberRequest.getUniqueUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + inviteMemberRequest.getUniqueUsername() + " not found"));

        if (invitee.equals(project.getOwner())) {
            throw new BadRequestException("Owner cannot be invited as a member");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new AlreadyExistsException("User %s is already a member of the project".formatted(inviteMemberRequest.getUniqueUsername()));
        }

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(inviteMemberRequest.getRole())
                .invitedAt(LocalDateTime.now())
                .build();

        emailNotificationService.sendNotification(inviteMemberRequest.getUniqueUsername(), "You have been invited to join the project: " + project.getName());

        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toMemberResponse(projectMember);
    }

    @Override
    public MemberResponse updateMemberRole(String email, Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest) {
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("Only owner can update member role");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member with id " + memberId + " not found in project with id " + projectId));


        projectMember.setProjectRole(updateMemberRoleRequest.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toMemberResponse(projectMember);
    }

    @Override
    public Void removeMember(String email, Long projectId, Long memberId) {
        Project project = getAccessibleProject(email, projectId);

        if (!project.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("Only owner can remove member");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new ResourceNotFoundException("Member with id " + memberId + " not found in project with id " + projectId);
        }
        projectMemberRepository.deleteById(projectMemberId);

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
