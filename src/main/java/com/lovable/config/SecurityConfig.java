package com.lovable.config;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.config
 * Created by: Ashish Kushwaha on 10-07-2026 16:28
 * File: SecurityConfig
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String REMEMBER_ME_KEY;

    public SecurityConfig(
            @Value("${app.security.remember-me-key}")
            String rememberMeKey
    ) {
        REMEMBER_ME_KEY = rememberMeKey;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorised -> {
                    authorised.requestMatchers("/v1/auth/**").permitAll();
                    authorised.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.NEVER);
                    session.maximumSessions(1);
                    session.sessionFixation().migrateSession();
                    session.invalidSessionUrl("/v1/auth/logout");
                    session.invalidSessionStrategy((_, response) -> {
                        response.setStatus(
                                HttpServletResponse.SC_UNAUTHORIZED
                        );
                        response.setContentType(
                                "application/json"
                        );
                        response.getWriter().write("""
                                {
                                    "message": "Invalid or expired session"
                                }
                                """);
                    });
                    session.sessionConcurrency(sessionConcurrency -> {
                        sessionConcurrency.maximumSessions(1);
                        sessionConcurrency.maxSessionsPreventsLogin(true);
                        sessionConcurrency.expiredSessionStrategy(event -> {
                            HttpServletResponse response =
                                    event.getResponse();
                            response.setStatus(
                                    HttpServletResponse.SC_UNAUTHORIZED
                            );
                            response.setContentType(
                                    "application/json"
                            );
                            response.getWriter().write("""
                                    {
                                        "message": "Session expired because the maximum number of sessions was exceeded"
                                    }
                                    """);
                        });
                    });
                })
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(
//                                CookieCsrfTokenRepository.withHttpOnlyFalse()
//                        )
//                        .ignoringRequestMatchers(
//                                "/v1/auth/login",
//                                "/v1/auth/signup"
//                        )
//                )
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .rememberMe(remember -> remember
                        .rememberMeCookieName("REMEMBER_ME")
                        .tokenValiditySeconds(
                                (int) Duration.ofDays(30).getSeconds()
                        )
                        .alwaysRemember(false)
                        .key(REMEMBER_ME_KEY)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((_, response, _) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                    {
                                        "message": "Unauthorized access"
                                    }
                                    """);
                        })
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> {
                    logout.logoutUrl("/v1/auth/logout");
                    logout.invalidateHttpSession(true);
                    logout.deleteCookies("JSESSIONID");
                    logout.clearAuthentication(true);
                    logout.logoutSuccessHandler((_, response, _) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write("""
                                {
                                    "message": "Logout successful"
                                }
                                """);
                    });
                    logout.permitAll();
                })
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
            SessionRegistry sessionRegistry
    ) {

        ConcurrentSessionControlAuthenticationStrategy concurrent =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);

        concurrent.setMaximumSessions(1);
        concurrent.setExceptionIfMaximumExceeded(true);

        SessionFixationProtectionStrategy fixation =
                new SessionFixationProtectionStrategy();

        RegisterSessionAuthenticationStrategy register =
                new RegisterSessionAuthenticationStrategy(sessionRegistry);

        return new CompositeSessionAuthenticationStrategy(
                List.of(
                        concurrent,
                        fixation,
                        register
                )
        );
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
