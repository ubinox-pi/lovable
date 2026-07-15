package com.lovable.service.impls;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 15-07-2026 20:09
 * File: UsageServiceV1Impl
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageServiceV1Impl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsage(String email) {
        return null;
    }

    @Override
    public PLanLimitsResponse getPlanLimits(String email) {
        return null;
    }
}
