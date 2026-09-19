package com.secureflow.apigateway.config;

import com.secureflow.apigateway.exception.GatewayAuthenticationEntryPoint;
import com.secureflow.apigateway.security.CorrelationIdFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final GatewayAuthenticationEntryPoint authenticationEntryPoint;
    private final CorrelationIdFilter correlationIdFilter;

    public SecurityConfig(
            GatewayAuthenticationEntryPoint authenticationEntryPoint,
            CorrelationIdFilter correlationIdFilter
    ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.correlationIdFilter = correlationIdFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login",
                                        "/api/auth/refresh",
                                        "/api/auth/logout",
                                        "/actuator/health"
                                ).permitAll()

                                .anyRequest().authenticated()
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .authenticationEntryPoint(
                                        authenticationEntryPoint
                                )
                                .jwt(jwt -> {})
                )

                .addFilterBefore(
                        correlationIdFilter,
                        BearerTokenAuthenticationFilter.class
                );

        return http.build();
    }
}