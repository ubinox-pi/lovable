package com.lovable.service.impls;

import com.lovable.dto.project.ProjectDto;
import com.lovable.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 10-07-2026 17:10
 * File: ProjectServiceImplV1
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
public class ProjectServiceImplV1 implements ProjectService {
    @Override
    public List<ProjectDto> getUserAllProject(String email) {
        return List.of();
    }

    @Override
    public ProjectDto getUserProjectById(Long id, String email) {
        return null;
    }

    @Override
    public ProjectDto createProject(String email, ProjectDto projectDto) {
        return null;
    }

    @Override
    public ProjectDto updateProject(String email, Long id, ProjectDto projectDto) {
        return null;
    }

    @Override
    public Void deleteProject(String email, Long id) {
        return null;
    }
}
