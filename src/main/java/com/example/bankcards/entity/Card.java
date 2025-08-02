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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String number;

    private String numberHash;

    private String last4;

    private LocalDate expirationDate;

    @Column(columnDefinition = "DEFAULT 'active'", insertable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @JoinColumn(name = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private CardInformation information;
}
