package com.lovable.service;

import com.lovable.dto.member.InviteMemberRequest;
import com.lovable.dto.member.MemberResponse;
import com.lovable.dto.member.UpdateMemberRoleRequest;

import java.util.List;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service
 * Created by: Ashish Kushwaha on 15-07-2026 18:50
 * File: ProjectMemberService
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(String email, Long projectId);

    MemberResponse inviteMember(String email, Long projectId, InviteMemberRequest inviteMemberRequest);

    MemberResponse updateMemberRole(String email, Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest);

    Void removeMember(String email, Long projectId, Long memberId);
}
