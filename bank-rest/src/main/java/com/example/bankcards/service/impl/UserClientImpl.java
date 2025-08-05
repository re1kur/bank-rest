package com.example.bankcards.service.impl;

import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.service.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {
    private final RestTemplate template;

    @Value("${spring.authz.uri}")
    private String URI;

    @Override
    public void checkIfExists(UUID userId) {
        try {
            ResponseEntity<Void> response = template.exchange(
                    "%s/%s".formatted(URI, userId),
                    HttpMethod.GET,
                    null,
                    Void.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new UserNotFoundException("User [%s] was not found.".formatted(userId));
            }
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException("User [%s] was not found.".formatted(userId));
        }
    }
}
