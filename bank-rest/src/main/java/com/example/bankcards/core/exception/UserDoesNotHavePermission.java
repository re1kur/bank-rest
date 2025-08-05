package com.example.bankcards.core.exception;

public class UserDoesNotHavePermission extends RuntimeException {
    public UserDoesNotHavePermission(String message) {
        super(message);
    }
}
