package com.example.bankcards.service;


import java.util.UUID;

public interface UserClient {
    void checkIfExists(UUID userId);
}
