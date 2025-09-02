package com.example.bankcards.core.exception;

public class TokenIsInvalidException extends RuntimeException {
    public TokenIsInvalidException(String message) {
        super(message);
    }
}
