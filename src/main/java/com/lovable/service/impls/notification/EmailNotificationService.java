package com.lovable.service.impls.notification;

import com.lovable.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.notification
 * Created by: Ashish Kushwaha on 17-07-2026 16:51
 * File: EmailNotificationService
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
@Async
public class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(String email, String message) {
        if (email == null || message == null) {
            log.error("Email or message is null. Cannot send notification.");
            return;
        }

        if (email.isEmpty() || message.isEmpty()) {
            log.error("Email or message is empty. Cannot send notification.");
            return;
        }

        if (!email.contains("@")) {
            log.error("Invalid email address: {}. Cannot send notification.", email);
            return;
        }

        log.info("Sending email to {} with message: {}", email, message);
    }
}
