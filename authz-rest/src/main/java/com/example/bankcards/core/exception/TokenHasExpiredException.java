package com.example.bankcards.core.exception;

public class TokenHasExpiredException extends RuntimeException {
    public TokenHasExpiredException(String message) {
        super(message);
    }
}
