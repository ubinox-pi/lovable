package com.lovable.repository;

import com.lovable.entity.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.repository
 * Created by: Ashish Kushwaha on 27-07-2026 19:34
 * File: SubscriptionsRepository
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {
}
