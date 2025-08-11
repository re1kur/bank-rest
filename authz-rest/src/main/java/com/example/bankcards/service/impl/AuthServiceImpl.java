package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.auth.LoginRequest;
import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.core.dto.auth.RefreshTokenPayload;
import com.example.bankcards.core.dto.auth.RegisterRequest;
import com.example.bankcards.core.exception.*;
import com.example.bankcards.entity.cache.RefreshToken;
import com.example.bankcards.entity.sql.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.sql.UserRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.RefreshTokenService;
import com.example.bankcards.util.JwtProvider;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository repo;
    private final UserMapper mapper;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService tokenService;

    @Value("${jwt.publicKeyPath}")
    private String publicKeyPath;

    @Value("${jwt.kidPath}")
    private String kidPath;

    @Override
    @Transactional
    public UUID register(RegisterRequest request) {
        String username = request.username();
        log.info("REGISTER REQUEST: [{}]", username);

        if (repo.existsByUsername(username))
            throw new UserAlreadyExistsException("User [%s] already exists.".formatted(username));

        User mapped = mapper.register(request);

        User saved = repo.save(mapped);

        log.info("REGISTER REQUEST 200: [{}]", saved.getId());

        return saved.getId();
    }

    @Override
    @Transactional
    public JwtPair login(LoginRequest request) {
        String username = request.username();
        log.info("LOGIN REQUEST: [{}]", username);

        User found = repo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User [%s] was not found.".formatted(username)));

        mapper.login(found, request);

        JwtPair response = jwtProvider.provide(found);

        log.info("LOGIN REQUEST 200: [{}]", found.getId());

        return response;
    }

    @Override
    @Transactional
    public void logout(RefreshTokenPayload request) {
        String token = request.value();

        try {
            log.info("LOGOUT: [{}]", request.value());

            SignedJWT refreshToken = SignedJWT.parse(token);
            UUID userId = UUID.fromString(refreshToken.getJWTClaimsSet().getSubject());

            tokenService.delete(userId);

            log.info("LOGOUT SUCCESSFUL: [{}]", token);
        } catch (ParseException e) {
            throw new TokenIsInvalidException("Refresh token [%s] is invalid.".formatted(token));
        }
    }

    @Override
    public JwtPair refresh(RefreshTokenPayload payload) {
        String token = payload.value();
        try {
            log.info("REFRESH ACCESS TOKEN REQUEST: [{}]", token);

            SignedJWT refreshToken = SignedJWT.parse(token);
            UUID userId = UUID.fromString(refreshToken.getJWTClaimsSet().getSubject());

            RefreshToken found = tokenService.get(userId);

            checkConflicts(found, refreshToken);

            User user = repo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User [%s] was not found.".formatted(userId)));

            JwtPair provided = jwtProvider.provide(user);

            log.info("REFRESH ACCESS TOKEN SUCCESSFUL: [user: {}]", userId);
            return provided;
        } catch (ParseException e) {
            throw new TokenIsInvalidException("Refresh token [%s] is invalid.".formatted(token));
        }
    }

    @Override
    public Map<String, Object> getJwks() {
        String kid = jwtProvider.readKidFromFile(kidPath);
        RSAPublicKey publicKey = jwtProvider.readPublicKeyFromFile(publicKeyPath);

        RSAKey publicJwk = new RSAKey.Builder(publicKey)
                .keyID(kid)
                .build();
        return Map.of("keys", List.of(publicJwk.toPublicJWK().toJSONObject()));
    }

    private void checkConflicts(RefreshToken found, SignedJWT refreshToken) throws ParseException {
        String token = refreshToken.serialize();
        LocalDateTime dbExpiresAt = found.getExpiresAt();

        jwtProvider.verify(refreshToken);

        if (LocalDateTime.now().isAfter(dbExpiresAt))
            throw new TokenHasExpiredException("Refresh token [%s] has expired.".formatted(token));

        if (!found.getValue().equals(token))
            throw new TokenIsInvalidException("Refresh token [%s] is invalid.".formatted(token));


    }
}