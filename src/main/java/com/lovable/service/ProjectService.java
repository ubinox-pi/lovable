package com.lovable.service;

import com.lovable.dto.project.ProjectDto;

import java.util.List;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service
 * Created by: Ashish Kushwaha on 10-07-2026 17:09
 * File: ProjectService
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface ProjectService {
    List<ProjectDto> getUserAllProject(String email);

    ProjectDto getUserProjectById(String email, Long projectId);

    ProjectDto createProject(String email, ProjectDto projectDto);

    ProjectDto updateProject(String email, Long projectId, ProjectDto projectDto);

    Void deleteProject(String email, Long projectId);
}
