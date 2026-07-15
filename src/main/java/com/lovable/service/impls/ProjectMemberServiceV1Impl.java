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
import com.lovable.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectMemberServiceV1Impl implements ProjectMemberService {
    @Override
    public List<MemberResponse> getProjectMembers(String email, Long projectId) {
        return List.of();
    }

    @Override
    public MemberResponse inviteMember(String email, Long projectId, InviteMemberRequest inviteMemberRequest) {
        return null;
    }

    @Override
    public MemberResponse updateMemberRole(String email, Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest) {
        return null;
    }

    @Override
    public MemberResponse removeMember(String email, Long projectId, Long memberId) {
        return null;
    }
}
