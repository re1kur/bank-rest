package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "card_balances")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
    @Id
    private UUID cardId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(insertable = false, columnDefinition = "DEFAULT 0")
    private BigDecimal value;

    @Column(insertable = false, columnDefinition = "DEFAULT FALSE")
    private Boolean blocked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance balance)) return false;

        if (cardId == null || balance.cardId == null) {
            return false;
        }

        return cardId.equals(balance.cardId);
    }

    @Override
    public int hashCode() {
        return cardId != null ? cardId.hashCode() : System.identityHashCode(this);
    }
}
