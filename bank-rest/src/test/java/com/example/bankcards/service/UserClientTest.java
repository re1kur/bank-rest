package com.example.bankcards.service;

import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.service.impl.UserClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {
    @InjectMocks
    private UserClientImpl client;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "URI", "http://localhost:8081/api/v1/users");
    }


    @Test
    void checkIfExists_ShouldThrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        String url = "http://localhost:8081/api/v1/users/" + userId;

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class));

        assertThrows(UserNotFoundException.class, () -> client.checkIfExists(userId));
    }

}