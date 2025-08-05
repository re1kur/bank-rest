package com.example.bankcards.service.impl;

import com.example.bankcards.core.exception.RefreshTokenNotFoundException;
import com.example.bankcards.entity.cache.RefreshToken;
import com.example.bankcards.mapper.RefreshTokenMapper;
import com.example.bankcards.repository.cache.RefreshTokenRepository;
import com.example.bankcards.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final RefreshTokenMapper mapper;

    // todo: tests
    @Override
    @Transactional
    public void create(String signedRefreshToken, UUID userId, LocalDateTime expiresAt) {
        log.info("CREATE REFRESH TOKEN: [{}]", userId);

        Optional<RefreshToken> refreshToken = repo.findById(userId);
        refreshToken.ifPresent(repo::delete);

        RefreshToken mapped = mapper.create(signedRefreshToken, userId, expiresAt);

        RefreshToken saved = repo.save(mapped);

        log.info("REFRESH TOKEN CREATED: [{}]", saved.getId());
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("DELETE REFRESH TOKEN: [{}]", userId);

        RefreshToken found = repo.findById(userId)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token [%s] was not found.".formatted(userId)));

        repo.delete(found);

        log.info("REFRESH TOKEN DELETED: [{}]", userId);
    }

    @Override
    public RefreshToken get(UUID userId) {
        log.info("GET REFRESH TOKEN: [{}].", userId);

        RefreshToken found = repo.findById(userId)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token [%s] was not found.".formatted(userId)));

        log.info("REFRESH TOKEN FOUND AND GOT: [{}]", found.getId());
        return found;
    }
}
