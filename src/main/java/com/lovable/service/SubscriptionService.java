package com.lovable.service;

import com.lovable.dto.subscription.SubscriptionResponse;
import com.lovable.enums.SubscriptionStatus;

import java.time.LocalDateTime;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service
 * Created by: Ashish Kushwaha on 15-07-2026 19:45
 * File: SubscriptionService
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(String email);

    Void activateSubscription(String subscriptionId);

    Void updateSubscription(String subscriptionId, SubscriptionStatus status, LocalDateTime periodStart, LocalDateTime periodEnd, Boolean isActive);

    Void cancelSubscription(String subscriptionId);

    Void renewSubscriptionPeriod(String subscriptionId, LocalDateTime periodStart, LocalDateTime periodEnd);

    Void markSubscriptionPastDue(String subscriptionId);

    boolean canCreateNewProject();
}
