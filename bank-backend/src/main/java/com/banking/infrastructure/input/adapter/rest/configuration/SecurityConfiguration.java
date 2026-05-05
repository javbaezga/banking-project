package com.banking.infrastructure.input.adapter.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private static final String ALL_PATHS_PATTERN = "/**";

    private static void authorizeExchange(@NonNull final AuthorizeExchangeSpec authorizeExchangeSpec) {
        authorizeExchangeSpec.pathMatchers(HttpMethod.GET, ALL_PATHS_PATTERN).permitAll()
            .pathMatchers(HttpMethod.POST, ALL_PATHS_PATTERN).permitAll()
            .pathMatchers(HttpMethod.PUT, ALL_PATHS_PATTERN).permitAll()
            .pathMatchers(HttpMethod.PATCH, ALL_PATHS_PATTERN).permitAll()
            .pathMatchers(HttpMethod.DELETE, ALL_PATHS_PATTERN).permitAll()
            .anyExchange()
            .authenticated();
    }

    @NonNull
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(@NonNull final ServerHttpSecurity http) {
        return http.csrf(CsrfSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .logout(LogoutSpec::disable)
            .authorizeExchange(SecurityConfiguration::authorizeExchange)
            .build();
    }
}
