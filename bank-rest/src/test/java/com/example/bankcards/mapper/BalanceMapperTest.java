package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.entity.Balance;
import com.example.bankcards.mapper.impl.BalanceMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BalanceMapperTest {
    @InjectMocks
    private BalanceMapperImpl balanceMapper;

    @Test
    void read_ShouldMapBalanceToDto() {
        UUID cardId = UUID.randomUUID();
        BigDecimal value = new BigDecimal("1000.50");
        boolean blocked = true;

        Balance balance = Balance.builder()
                .cardId(cardId)
                .value(value)
                .blocked(blocked)
                .build();

        BalanceDto dto = balanceMapper.read(balance);

        assertThat(dto.cardId()).isEqualTo(cardId);
        assertThat(dto.value()).isEqualByComparingTo(value);
        assertThat(dto.blocked()).isEqualTo(blocked);
    }

    @Test
    void update_ShouldUpdateBalanceFields() {
        Balance balance = Balance.builder()
                .cardId(UUID.randomUUID())
                .value(new BigDecimal("500.00"))
                .blocked(false)
                .build();

        BalanceUpdatePayload payload = new BalanceUpdatePayload(
                new BigDecimal("750.00"),
                true
        );

        Balance updated = balanceMapper.update(balance, payload);

        assertThat(updated.getValue()).isEqualByComparingTo("750.00");
        assertThat(updated.getBlocked()).isTrue();
        assertThat(updated).isSameAs(balance);
    }

    @Test
    void update_ShouldKeepBlockedWhenNull() {
        Balance balance = Balance.builder()
                .cardId(UUID.randomUUID())
                .value(new BigDecimal("100.00"))
                .blocked(true)
                .build();

        BalanceUpdatePayload payload = new BalanceUpdatePayload(
                new BigDecimal("120.00"),
                null
        );

        Balance updated = balanceMapper.update(balance, payload);

        assertThat(updated.getValue()).isEqualByComparingTo("120.00");
        assertThat(updated.getBlocked()).isTrue();
    }
}

