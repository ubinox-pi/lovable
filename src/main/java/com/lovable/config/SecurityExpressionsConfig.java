package com.lovable.config;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.config
 * Created by: Ashish Kushwaha on 22-07-2026 16:29
 * File: SecurityExpressionsConfig
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.enums.ProjectPermission;
import com.lovable.repository.ProjectMemberRepository;
import com.lovable.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressionsConfig {

    private final ProjectMemberRepository projectMemberRepository;

    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW);
    }

    private boolean hasPermission(Long projectId, ProjectPermission permission) {
        String email = AppUtils.getCurrentUserEmail();
        return projectMemberRepository.findRoleByProjectIdAndEmail(projectId, email)
                .map(role -> role.getPermissionSet().contains(permission))
                .orElse(false);
    }

    public boolean canManageMembers(Long projectId) {
        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);
    }

    public boolean canViewMembers(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canEditProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.EDIT);
    }
}
