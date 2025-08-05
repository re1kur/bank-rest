package com.example.bankcards.entity;

import com.example.bankcards.core.dto.card.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "cards")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private String number;

    private String numberHash;

    private String last4;

    private LocalDate expirationDate;

    @Column(columnDefinition = "DEFAULT 'active'", insertable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @OneToOne(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CardInformation information;

    @OneToOne(mappedBy = "card", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Balance balance;
}
