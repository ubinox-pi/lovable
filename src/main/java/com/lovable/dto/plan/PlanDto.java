package com.lovable.dto.plan;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.dto.plan
 * Created by: Ashish Kushwaha on 15-07-2026 19:50
 * File: PlanDto
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDto {

    @Positive
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Max projects cannot be null")
    @Positive(message = "Max projects must be a positive value")
    private Integer maxProjects;

    @NotNull(message = "Max token per day cannot be null")
    @Positive(message = "Max token per day must be a positive value")
    private Integer maxTokenPerDay;

    @NotNull(message = "Max previews cannot be null")
    @Positive(message = "Max previews must be a positive value")
    private Integer maxPreviews;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Unlimited AI cannot be null")
    private Boolean unlimitedAi;

    @NotNull(message = "Active status cannot be null")
    private Boolean isActive;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive value")
    private Integer price;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotBlank(message = "Period cannot be blank")
    private String period;

    @NotNull(message = "Interval cannot be null")
    @Positive(message = "Interval must be a positive value")
    private Integer interval;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
