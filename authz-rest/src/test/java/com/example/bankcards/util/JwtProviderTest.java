package com.example.bankcards.util;

import com.example.bankcards.entity.sql.Role;
import com.example.bankcards.entity.sql.User;
import com.example.bankcards.util.impl.JwtProviderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {
    @InjectMocks
    private JwtProviderImpl provider;

    @Test
    void provide__ReturnsTokens() {
        UUID userId = UUID.randomUUID();
        Role userRole = Role.builder().id(1).name("USER").build();
        User user = User.builder()
                .id(userId)
                .username("username")
                .password("password")
                .roles(List.of(userRole)).build();


    }
}