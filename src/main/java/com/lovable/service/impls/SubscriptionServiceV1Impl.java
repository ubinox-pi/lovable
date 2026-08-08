package com.lovable.service.impls;

import com.lovable.dto.subscription.SubscriptionResponse;
import com.lovable.entity.Subscriptions;
import com.lovable.entity.User;
import com.lovable.enums.SubscriptionStatus;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.mapper.SubscriptionMapper;
import com.lovable.repository.SubscriptionRepository;
import com.lovable.repository.UserRepository;
import com.lovable.service.SubscriptionService;
import com.lovable.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 15-07-2026 19:45
 * File: SubscriptionServiceV1Impl
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceV1Impl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final Integer freeProjectLimit = 1;

    @Override
    public SubscriptionResponse getCurrentSubscription(String email) {
        return subscriptionRepository.findByUserEmailWithActiveSubscriptions(email)
                .map(subscriptionMapper::toSubscriptionResponse)
                .orElse(SubscriptionResponse.builder()
                        .plan(null)
                        .status(SubscriptionStatus.NONEXISTENT)
                        .currentPeriodEnd(null)
                        .tokenUsedThisCycle(null)
                        .build());
    }

    @Override
    public Void updateSubscription(String subscriptionId, SubscriptionStatus status, LocalDateTime periodStart, LocalDateTime periodEnd, Boolean isActive) {
        return null;
    }

    @Override
    public Void cancelSubscription(String subscriptionId) {
        return null;
    }

    @Override
    public Void renewSubscriptionPeriod(String subscriptionId, LocalDateTime periodStart, LocalDateTime periodEnd) {

        User user = userRepository.getReferenceById(AppUtils.getCurrentUserId());

        Subscriptions subscriptions = getSubscription(subscriptionId, user);

        LocalDateTime currentPeriodStart = periodStart != null ? periodStart : subscriptions.getCurrentPeriodEnd();
        LocalDateTime currentPeriodEnd = periodEnd != null ? periodEnd : subscriptions.getCurrentPeriodStart();
        subscriptions.setCurrentPeriodStart(currentPeriodStart);
        subscriptions.setCurrentPeriodEnd(currentPeriodEnd);

        if (subscriptions.getStatus() == SubscriptionStatus.PAST_DUE) {
            subscriptions.setStatus(SubscriptionStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public Void markSubscriptionPastDue(String subscriptionId) {
        return null;
    }

    @Override
    public boolean canCreateNewProject() {
        User currentUser = userRepository.getReferenceById(AppUtils.getCurrentUserId());
        if (currentUser.getProjects().isEmpty() || currentUser.getProjects().size() < freeProjectLimit) {
            return true;
        }
        Subscriptions subscription = subscriptionRepository.findByUserEmailWithActiveSubscriptions(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));
        return subscription.getPlan().getMaxProjects() >
                currentUser.getProjects()
                        .stream()
                        .filter(project -> project.getOwner().equals(currentUser))
                        .count();
    }

    private @NonNull Subscriptions getSubscription(String subscriptionId, User user) {
        return subscriptionRepository.findSubscriptionsBySubscriptionIdAndUser(subscriptionId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }

    // TODO: To be completed
}
