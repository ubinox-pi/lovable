package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 15-07-2026 18:48
 * File: ProjectMemberController
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
import com.lovable.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectMemberService.getProjectMembers(email, projectId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable Long projectId,
            @RequestBody InviteMemberRequest inviteMemberRequest
    ) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectMemberService.inviteMember(email, projectId, inviteMemberRequest), HttpStatus.CREATED);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody UpdateMemberRoleRequest updateMemberRoleRequest
    ) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectMemberService.updateMemberRole(email, projectId, memberId, updateMemberRoleRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectMemberService.removeMember(email, projectId, memberId), HttpStatus.NO_CONTENT);
    }
}
