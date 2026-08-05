package com.lovable.controller;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.controller
 * Created by: Ashish Kushwaha on 15-07-2026 19:13
 * File: BillingController
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import com.lovable.dto.subscription.CheckoutRequest;
import com.lovable.dto.subscription.CheckoutResponse;
import com.lovable.dto.subscription.PortalResponse;
import com.lovable.dto.subscription.SubscriptionResponse;
import com.lovable.service.PaymentProcessorService;
import com.lovable.service.SubscriptionService;
import com.lovable.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PaymentProcessorService paymentProcessorService;

    @GetMapping("/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription() {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(subscriptionService.getCurrentSubscription(email), HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(@RequestBody CheckoutRequest checkoutRequest) {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(paymentProcessorService.createCheckoutSession(email, checkoutRequest), HttpStatus.OK);
    }

    @PostMapping("/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {
        String email = AppUtils.getCurrentUserEmail();
        return new ResponseEntity<>(paymentProcessorService.openCustomerPortal(email), HttpStatus.OK);
    }
}
