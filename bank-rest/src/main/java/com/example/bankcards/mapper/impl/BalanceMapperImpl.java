package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.entity.Balance;
import com.example.bankcards.mapper.BalanceMapper;

@Mapper
public class BalanceMapperImpl implements BalanceMapper {
    @Override
    public BalanceDto read(Balance balance) {
        return BalanceDto.builder()
                .cardId(balance.getCardId())
                .value(balance.getValue())
                .blocked(balance.getBlocked())
                .build();
    }

    @Override
    public Balance update(Balance balance, BalanceUpdatePayload payload) {
        balance.setValue(payload.value());
        if (payload.blocked() != null)
            balance.setBlocked(payload.blocked());

        return balance;
    }
}
