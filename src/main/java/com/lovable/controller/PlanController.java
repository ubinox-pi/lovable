package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 24-07-2026 13:27
 * File: PlanController
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.plan.PlanDto;
import com.lovable.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/plans")
@RestController
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        return new ResponseEntity<>(planService.getAllPlans(), HttpStatus.OK);
    }

    @PostMapping
//    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<PlanDto> createPlan(@RequestBody PlanDto planDto) {
        return new ResponseEntity<>(planService.createPlan(planDto), HttpStatus.CREATED);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanDto> getPlanById(@PathVariable Long planId) {
        return new ResponseEntity<>(planService.getPlanById(planId), HttpStatus.OK);
    }
}
