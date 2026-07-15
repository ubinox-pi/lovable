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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                        sessionConcurrency.maxSessionsPreventsLogin(false);
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
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
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
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
