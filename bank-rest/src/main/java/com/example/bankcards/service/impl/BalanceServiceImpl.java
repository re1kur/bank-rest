package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.core.exception.BalanceNotFoundException;
import com.example.bankcards.entity.Balance;
import com.example.bankcards.mapper.BalanceMapper;
import com.example.bankcards.repository.BalanceRepository;
import com.example.bankcards.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository repo;
    private final BalanceMapper mapper;

    @Override
    public BalanceDto read(UUID cardId) {
        return repo.findById(cardId).map(mapper::read)
                .orElseThrow(() -> new BalanceNotFoundException("Balance [%s] was not found.".formatted(cardId)));
    }

    @Override
    @Transactional
    public void update(UUID cardId, BalanceUpdatePayload payload) {
        log.info("UPDATE BALANCE REQUEST: [card: {}]", cardId);
        Balance found = repo.findById(cardId)
                .orElseThrow(() -> new BalanceNotFoundException("Balance [%s] was not found.".formatted(cardId)));

        checkConflicts(found, payload);

        Balance mapped = mapper.update(found, payload);

        Balance saved = repo.save(mapped);

        log.info("BALANCE UPDATED: [card: {}]", saved.getCardId());
    }

    private void checkConflicts(Balance balance, BalanceUpdatePayload payload) {
//        if (balance.getBlocked())
//            throw new BalanceIsBlockedException("Balance is blocked");
    }
}
