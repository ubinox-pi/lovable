package com.lovable.repository;

import com.lovable.entity.ProjectMember;
import com.lovable.entity.ProjectMemberId;
import com.lovable.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.repository
 * Created by: Ashish Kushwaha on 16-07-2026 21:06
 * File: ProjectMemberRepository
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findByIdProjectId(Long idProjectId);

    @Query("""
            SELECT pm.projectRole FROM ProjectMember pm, User u
            WHERE u.email = :email AND pm.id.projectId = :projectId
                        AND pm.id.memberId = u.id
            """)
    Optional<ProjectRole> findRoleByProjectIdAndEmail(Long projectId, String email);
}
