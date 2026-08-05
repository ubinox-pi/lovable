package com.lovable.service.impls.subscriptionsWebhook;

import com.lovable.service.ProcessSubscriptionWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls.subscriptionsWebhook
 * Created by: Ashish Kushwaha on 27-07-2026 19:51
 * File: RazorpaySubscriptionProcess
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class RazorpaySubscriptionProcess implements ProcessSubscriptionWebhook {

    @Override
    public Void process(Object body) {
        log.debug("Razorpay Subscription Webhook Received: {}", body);

        if (!(body instanceof String json)) {
            log.warn("Ignoring Razorpay webhook payload because it is not a JSON string: {}", body);
            return null;
        }

        JSONObject parsedBody = new JSONObject(json);

        String event = parsedBody.optString("event", "");

        JSONObject payload = parsedBody.optJSONObject("payload");

        switch (event) {

            case "subscription.created" -> {
                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.info("Subscription created: {}", subscription);

                String subscriptionId = subscription.getString("id");
                String planId = subscription.getString("plan_id");
                String status = subscription.getString("status");

                // TODO: Save subscription if required
            }

            case "subscription.activated" -> {
                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.info("Subscription activated: {}", subscription);

                String subscriptionId = subscription.getString("id");
                String status = subscription.getString("status");
                long currentStart = subscription.optLong("current_start");
                long currentEnd = subscription.optLong("current_end");

                // TODO:
                // Mark subscription ACTIVE in database
                // Save currentStart/currentEnd
            }

            case "subscription.charged" -> {
                JSONObject payment = payload
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.info("Subscription charged. Subscription={}, Payment={}",
                        subscription, payment);

                String subscriptionId = subscription.getString("id");
                String paymentId = payment.getString("id");
                String orderId = payment.optString("order_id");
                int amount = payment.getInt("amount");

                // TODO:
                // Record successful payment
            }

            case "subscription.pending" -> {
                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.info("Subscription pending: {}", subscription);

                // TODO: Update status to PENDING
            }

            case "subscription.halted" -> {
                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.warn("Subscription halted: {}", subscription);

                // TODO: Update status to HALTED
            }

            case "subscription.cancelled" -> {
                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.info("Subscription cancelled: {}", subscription);

                // TODO: Mark subscription CANCELLED
            }

            case "subscription.completed" -> {
                JSONObject subscription = payload
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

                log.info("Subscription completed: {}", subscription);

                // TODO: Mark subscription COMPLETED
            }
 
            default -> log.debug("Ignoring unsupported Razorpay event: {}", event);
        }

        return null;
    }
}
