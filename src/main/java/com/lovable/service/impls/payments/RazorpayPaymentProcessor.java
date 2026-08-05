package com.lovable.service.impls.payments;

import com.lovable.dto.subscription.CheckoutRequest;
import com.lovable.dto.subscription.CheckoutResponse;
import com.lovable.dto.subscription.PortalResponse;
import com.lovable.entity.PaymentProviderPlan;
import com.lovable.entity.Plan;
import com.lovable.entity.Subscriptions;
import com.lovable.entity.User;
import com.lovable.enums.PaymentProvider;
import com.lovable.enums.SubscriptionStatus;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.repository.PaymentProviderPlanRepository;
import com.lovable.repository.PlanRepository;
import com.lovable.repository.SubscriptionsRepository;
import com.lovable.repository.UserRepository;
import com.lovable.service.PaymentProcessorService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Subscription;
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
 * Package: com.lovable.service.impls.payments
 * Created by: Ashish Kushwaha on 24-07-2026 13:09
 * File: RazorpayPaymentProcessor
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
public class RazorpayPaymentProcessor implements PaymentProcessorService {

    private final PlanRepository planRepository;
    private final RazorpayClient razorpayClient;
    private final PaymentProviderPlanRepository paymentProviderPlanRepository;
    private final UserRepository userRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    @Override
    public CheckoutResponse createCheckoutSession(String email, CheckoutRequest checkoutRequest) {
        log.info("Creating checkout session for email: {}", email);
        Plan plan = planRepository.findById(checkoutRequest.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan with id " + checkoutRequest.getPlanId() + " not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        PaymentProviderPlan paymentProviderPlan = paymentProviderPlanRepository.findByPlan(plan)
                .orElseThrow(() -> new ResourceNotFoundException("Payment provider plan for plan " + plan.getId() + " not found"));

        JSONObject request = new JSONObject();
        request.put("plan_id", paymentProviderPlan.getProviderPlanId());
        request.put("total_count", 120);
        request.put("customer_notify", 1);

        Subscription razorpaySubscription;
        try {
            razorpaySubscription = razorpayClient
                    .subscriptions
                    .create(request);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }

        Subscriptions subscription = Subscriptions.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.INCOMPLETE)
                .provider(PaymentProvider.RAZORPAY)
                .customerId(user.getEmail())
                .subscriptionId(razorpaySubscription.get("id"))
                .currentPeriodStart(null)
                .currentPeriodEnd(null)
                .canceledAtPeriodEnd(null)
                .canceledAt(null)
                .endedAt(null)
                .build();
        subscriptionsRepository.save(subscription);

        return CheckoutResponse.builder()
                .checkoutUrl(razorpaySubscription.get("id"))
                .build();
    }

    @Override
    public PortalResponse openCustomerPortal(String email) {
        return null;
    }
}
