package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 15-07-2026 20:07
 * File: UsageController
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.subscription.PLanLimitsResponse;
import com.lovable.dto.subscription.UsageTodayResponse;
import com.lovable.service.UsageService;
import com.lovable.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/usage")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage() {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(usageService.getTodayUsage(email), HttpStatus.OK);
    }

    @GetMapping("/limits")
    public ResponseEntity<PLanLimitsResponse> getPLanLimits() {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(usageService.getPlanLimits(email), HttpStatus.OK);
    }
}
