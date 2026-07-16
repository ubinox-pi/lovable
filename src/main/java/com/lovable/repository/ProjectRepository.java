package com.lovable.repository;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.repository
 * Created by: Ashish Kushwaha on 16-07-2026 13:50
 * File: ProjectRepository
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            SELECT p FROM Project p
            WHERE p.deletedAt IS NULL
            AND p.owner.email = :email
            ORDER BY p.updatedAt DESC
            """)
        // TODO: handle members
    List<Project> findAllAccessibleProjectsByUser(String email);

    @Query("""
            SELECT p FROM Project p
            WHERE p.deletedAt IS NULL
            AND p.id = :projectId
            AND p.owner.email = :email
            """)
    Optional<Project> findByProjectIdAndUserEmail(Long projectId, String email);
}
