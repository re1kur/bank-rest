package com.example.bankcards.service;

import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.service.impl.UserClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserClientTest {
    @InjectMocks
    private UserClientImpl client;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void checkIfExists_ShouldThrowException_WhenUserNotFound() {
        String bearer = "jwtBearer";
        UUID userId = UUID.randomUUID();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(restTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Void.class));

        assertThrows(UserNotFoundException.class, () -> client.checkIfExists(userId, bearer));
    }

}