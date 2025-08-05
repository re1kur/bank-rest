package com.example.bankcards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri(jwkSetUri)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/users/**",
                                "/api/v1/users",
                                "/api/v1/roles/**",
                                "/api/v1/roles"
                        ).hasAuthority("ADMIN")
                        .requestMatchers(
                                "/api/v1/profile"
                        )
                        .hasAuthority("USER")
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName("roles");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();
    }
}