package com.example.bankcards.controller;

import com.example.bankcards.core.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = exception.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> "%s: %s"
                        .formatted(error.getField(), error.getDefaultMessage()))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("errors", errors);


        log.info("ADVICE VALIDATION FAILED: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }

    private static Map<String, Object> getSimpleResponseMap(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();

        response.put("status", status.value());
        response.put("message", message);
        return response;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> response = getSimpleResponseMap(exception.getMessage(), status);

        log.info("ADVICE USER NOT FOUND: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCardNotFoundException(CardNotFoundException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> response = getSimpleResponseMap(exception.getMessage(), status);

        log.info("ADVICE CARD NOT FOUND: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBalanceNotFoundException(BalanceNotFoundException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> response = getSimpleResponseMap(exception.getMessage(), status);

        log.info("ADVICE BALANCE NOT FOUND: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionNotFoundException(TransactionNotFoundException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> response = getSimpleResponseMap(exception.getMessage(), status);

        log.info("ADVICE TRANSACTION NOT FOUND: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCardAlreadyExistsException(CardAlreadyExistsException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        Map<String, Object> response = getSimpleResponseMap(exception.getMessage(), status);

        log.info("ADVICE CARD CONFLICT: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserDoesNotHavePermission.class)
    public ResponseEntity<Map<String, Object>> handleUserDoesNotHavePermission(UserDoesNotHavePermission exception) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        Map<String, Object> response = getSimpleResponseMap(exception.getMessage(), status);

        log.info("ADVICE USER FORBIDDEN: [{}]", response);
        return ResponseEntity.status(status).body(response);
    }
}
