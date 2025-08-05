package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.entity.Balance;

public interface BalanceMapper {
    BalanceDto read(Balance balance);

    Balance update(Balance found, BalanceUpdatePayload payload);
}
