package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "card_balances")
@Entity
@Data
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
}
