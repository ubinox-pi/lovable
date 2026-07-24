package com.lovable.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.enums
 * Created by: Ashish Kushwaha on 08-07-2026 22:27
 * File: ProjectRole
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@RequiredArgsConstructor
@Getter
public enum ProjectRole {

    EDITOR(new ConcurrentSkipListSet<>(Set.of(ProjectPermission.VIEW, ProjectPermission.EDIT, ProjectPermission.VIEW_MEMBERS))),
    VIEWER(new ConcurrentSkipListSet<>(Set.of(ProjectPermission.VIEW, ProjectPermission.VIEW_MEMBERS))),
    OWNER(new ConcurrentSkipListSet<>(Set.of
            (
                    ProjectPermission.VIEW,
                    ProjectPermission.EDIT,
                    ProjectPermission.DELETE,
                    ProjectPermission.MANAGE_MEMBERS,
                    ProjectPermission.VIEW_MEMBERS)
    ));

    private final Set<ProjectPermission> permissionSet;
}
