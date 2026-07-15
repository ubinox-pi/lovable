package com.lovable.entity;

import com.lovable.enums.ProjectRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.entity
 * Created by: Ashish Kushwaha on 08-07-2026 22:21
 * File: ProjectMember
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMember {

    private ProjectMemberId id;

    private Project project;

    private User user;

    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;

    private LocalDateTime invitedAt;

    private LocalDateTime acceptedAt;
}
