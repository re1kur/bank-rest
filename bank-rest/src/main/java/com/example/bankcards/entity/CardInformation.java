package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "card_information")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardInformation {
    @Id
    private UUID cardId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;

    private String brand;

    @Column(insertable = false, columnDefinition = "DEFAULT CURRENT_DATE")
    private LocalDate issueDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardInformation info)) return false;

        if (cardId == null || info.cardId == null) {
            return false;
        }

        return cardId.equals(info.cardId);
    }

    @Override
    public int hashCode() {
        return cardId != null ? cardId.hashCode() : System.identityHashCode(this);
    }
}
