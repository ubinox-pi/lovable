package com.lovable.dto.member;

import com.lovable.enums.ProjectRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.dto.member
 * Created by: Ashish Kushwaha on 15-07-2026 19:09
 * File: UpdateMemberRoleRequest
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public record UpdateMemberRoleRequest(

        @NotBlank(message = "Role cannot be blank")
        @Enumerated(EnumType.STRING)
        ProjectRole role
) {
}
