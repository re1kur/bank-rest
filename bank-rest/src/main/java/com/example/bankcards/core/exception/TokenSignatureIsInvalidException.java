package com.example.bankcards.core.exception;

public class TokenSignatureIsInvalidException extends RuntimeException {
    public TokenSignatureIsInvalidException(String message) {
        super(message);
    }
}
