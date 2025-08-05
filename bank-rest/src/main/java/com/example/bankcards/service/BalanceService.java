package com.example.bankcards.service;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;

import java.util.UUID;

public interface BalanceService {
    BalanceDto read(UUID cardId);

    void update(UUID cardId, BalanceUpdatePayload payload);
}
