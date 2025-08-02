package com.example.bankcards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

@Configuration
public class VaultConfiguration {
    @Value("${spring.vault.token}")
    private String token;

    @Bean
    public VaultTemplate vaultTemplate(VaultEndpoint vaultEndpoint, ClientAuthentication clientAuthentication) {
        return new VaultTemplate(vaultEndpoint, clientAuthentication);
    }

    @Bean
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme("http");
        return endpoint;
    }

    @Bean
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(token);
    }
}

