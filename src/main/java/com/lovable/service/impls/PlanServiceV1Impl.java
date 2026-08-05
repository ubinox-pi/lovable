package com.lovable.service.impls;

import com.lovable.config.RazorpayProperties;
import com.lovable.dto.plan.PlanDto;
import com.lovable.entity.PaymentProviderPlan;
import com.lovable.entity.Plan;
import com.lovable.enums.PaymentProvider;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.mapper.PlanMapper;
import com.lovable.repository.PaymentProviderPlanRepository;
import com.lovable.repository.PlanRepository;
import com.lovable.service.PlanService;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 15-07-2026 19:45
 * File: PlanServiceV1Impl
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
public class PlanServiceV1Impl implements PlanService {
    private final PaymentProviderPlanRepository paymentProviderPlanRepository;
    private final PlanMapper planMapper;
    private final RazorpayProperties razorpayProperties;
    private final PlanRepository planRepository;
    private final WebClient webClient = WebClient.create("https://api.razorpay.com/v1/plans");

    @Override
    public List<PlanDto> getAllPlans() {
        log.info("Getting all plans");
        return planRepository.findAll()
                .stream()
                .map(planMapper::toPlanDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlanDto createPlan(PlanDto planDto) {
        log.info("Creating plan: {}", planDto);
        Plan plan = planMapper.toPlanEntity(planDto);

        plan = planRepository.save(plan);

        Map<String, Object> body = Map.of(
                "period", planDto.getPeriod(),
                "interval", planDto.getInterval(),
                "item", Map.of(
                        "name", planDto.getName(),
                        "description", planDto.getDescription(),
                        "amount", planDto.getPrice(),
                        "currency", planDto.getCurrency()
                )
        );

        String auth = Base64.getEncoder()
                .encodeToString((razorpayProperties.getKeyId() + ":" + razorpayProperties.getKeySecret()).getBytes());

        JsonNode response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body, JsonNode.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RazorpayException(
                                                "Razorpay API error: " + errorBody
                                        )
                                ))
                )
                .bodyToMono(JsonNode.class)
                .block();

        assert response != null;
        PaymentProviderPlan providerPlan = PaymentProviderPlan.builder()
                .plan(plan)
                .provider(PaymentProvider.RAZORPAY)
                .providerPlanId(response.get("id").asString())
                .build();
        
        log.info("Razorpay plan created: {}", response);
        paymentProviderPlanRepository.save(providerPlan);

        return planMapper.toPlanDto(plan);
    }

    @Override
    public PlanDto getPlanById(Long planId) {
        return planRepository.findById(planId)
                .map(planMapper::toPlanDto)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
    }
}
