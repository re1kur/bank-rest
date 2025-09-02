package com.example.bankcards.entity;

import com.example.bankcards.core.dto.card.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "cards")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Card card)) return false;

        return id.equals(card.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
