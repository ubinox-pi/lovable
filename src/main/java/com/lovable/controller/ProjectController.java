package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 10-07-2026 17:08
 * File: ProjectController
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.deploy.DeployResponse;
import com.lovable.dto.project.ProjectDto;
import com.lovable.service.DeploymentService;
import com.lovable.service.ProjectService;
import com.lovable.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final DeploymentService deploymentService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getMyProjects() {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectService.getUserAllProject(email), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long projectId) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectService.getUserProjectById(email, projectId), HttpStatus.OK);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectService.createProject(email, projectDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectDto projectDto) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectService.updateProject(email, projectId, projectDto), HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(projectService.deleteProject(email, projectId), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{projectId}/deploy")
    public ResponseEntity<DeployResponse> deployProject(@PathVariable Long projectId) {
        return new ResponseEntity<>(deploymentService.deploy(projectId), HttpStatus.OK);
    }
}
