package com.lovable.mapper;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.mapper
 * Created by: Ashish Kushwaha on 16-07-2026 22:17
 * File: ProjectMemberMapper
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.member.MemberResponse;
import com.lovable.entity.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mappings({
            @Mapping(source = "id.memberId", target = "userId"),
            @Mapping(source = "user.name", target = "name"),
            @Mapping(source = "user.uniqueUsername", target = "uniqueUsername"),
            @Mapping(source = "projectRole", target = "role"),
            @Mapping(source = "invitedAt", target = "invitedAt")
    })
    MemberResponse toMemberResponse(ProjectMember projectMember);
}
