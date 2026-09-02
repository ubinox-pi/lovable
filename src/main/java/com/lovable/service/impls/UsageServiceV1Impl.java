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

import com.lovable.dto.plan.PlanDto;
import com.lovable.dto.subscription.PLanLimitsResponse;
import com.lovable.dto.subscription.SubscriptionResponse;
import com.lovable.dto.subscription.UsageTodayResponse;
import com.lovable.entity.UsageLog;
import com.lovable.repository.UsageLogRepository;
import com.lovable.service.SubscriptionService;
import com.lovable.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageServiceV1Impl implements UsageService {

    private static final Long unlimitedAITokenThreshold = (long) ((Short.MAX_VALUE + Short.MAX_VALUE) * 2);
    private final UsageLogRepository usageLogRepository;
    private final SubscriptionService subscriptionService;

    @Override
    public Void recordDailyTokenUsage(String email, int actualToken) {
        usageLogRepository.findByEmailAndDate(email, LocalDate.now())
                .stream()
                .findFirst()
                .ifPresentOrElse(
                        usageLog -> {
                            usageLog.setTokenUsed(usageLog.getTokenUsed() + actualToken);
                            usageLogRepository.save(usageLog);
                        },
                        () -> {
                            usageLogRepository.save(UsageLog.builder()
                                    .email(email)
                                    .date(LocalDate.now())
                                    .tokenUsed((long) actualToken)
                                    .build());
                        }
                );
        return null;
    }

    @Override
    public Void checkDailyTokenUsage(String email) {
        SubscriptionResponse subscriptionResponse = subscriptionService.getCurrentSubscription(email);
        PlanDto plan = subscriptionResponse.getPlan();

        UsageLog usageLogToday = usageLogRepository.findByEmailAndDate(email, LocalDate.now())
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    UsageLog usageLog = UsageLog.builder()
                            .email(email)
                            .date(LocalDate.now())
                            .tokenUsed(0L)
                            .build();
                    usageLogRepository.save(usageLog);
                    return usageLog;
                });

        if (plan.getUnlimitedAi()) return null;
        if (usageLogToday.getTokenUsed() >= unlimitedAITokenThreshold)
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Daily token usage limit exceeded");


        int currentUsage = Math.toIntExact(usageLogToday.getTokenUsed());
        int limit = plan.getMaxTokenPerDay();

        if (currentUsage >= limit) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Daily token usage limit exceeded");
        }

        return null;
    }

    @Override
    public UsageTodayResponse getTodayUsage(String email) {
        return null;
    }

    @Override
    public PLanLimitsResponse getPlanLimits(String email) {
        return null;
    }
}
