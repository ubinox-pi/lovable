package com.lovable.repository;

import com.lovable.entity.Subscriptions;
import com.lovable.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.repository
 * Created by: Ashish Kushwaha on 06-08-2026 00:09
 * File: SubscriptionRepository
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscriptions, Long> {

    @Query("""
            SELECT s FROM Subscriptions s
            WHERE s.user.email = :email AND
            (s.status = SubscriptionStatus.ACTIVE OR
            s.status = SubscriptionStatus.TRAILING OR
            s.status = SubscriptionStatus.PAST_DUE)
            """)
    Optional<Subscriptions> findByUserEmailWithActiveSubscriptions(String email);

    Optional<Subscriptions> findSubscriptionsBySubscriptionIdAndUser(String subscriptionId, User user);
}
