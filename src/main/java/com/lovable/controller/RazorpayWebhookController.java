package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 27-07-2026 19:10
 * File: RazorpayWebhookController
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.config.RazorpayProperties;
import com.lovable.service.ProcessSubscriptionWebhook;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final RazorpayProperties razorpayProperties;
    private final ProcessSubscriptionWebhook processSubscriptionWebhook;

    @RequestMapping("/razorpay")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String body,
            @RequestHeader("X-Razorpay-Signature") String signature
    ) {
        try {
            Utils.verifyWebhookSignature(
                    body,
                    signature,
                    razorpayProperties.getWebhookSecret()
            );

            processSubscriptionWebhook.process(body);

        } catch (RazorpayException e) {
            throw new RuntimeException("Webhook verification failed: " + e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
