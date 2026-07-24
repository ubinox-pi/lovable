package com.lovable.enums;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.enums
 * Created by: Ashish Kushwaha on 23-07-2026 18:14
 * File: ProjectPermission
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPermission {
    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),
    MANAGE_MEMBERS("project:manage_members"),
    VIEW_MEMBERS("project:view_members");

    private final String permission;
}
