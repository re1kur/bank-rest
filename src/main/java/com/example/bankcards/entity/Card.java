package com.example.bankcards.entity;

import com.example.bankcards.core.dto.card.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private User user;

    private Byte[] number;

    private String lastNumbers;

    private LocalDateTime expirationDate;

    @Column(columnDefinition = "DEFAULT 'active'", insertable = false)
    private CardStatus status;

    private CardInformation information;
}
